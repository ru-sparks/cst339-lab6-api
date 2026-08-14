package com.sparkco.lab2_api.features.user;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lab 6 security configuration.
 *
 * <p>
 * <b>Branch 1:</b> open filter chain so Lab 4 routes stay reachable.
 * <p>
 * <b>Branch 2:</b> database-backed {@link UserDetailsService}.
 * <p>
 * <b>Branch 3:</b> {@code formLogin} uses the Thymeleaf {@code /login} page.
 * <p>
 * <b>Branch 4:</b> public matchers versus {@code authenticated()} for
 * everything else.
 * <p>
 * <b>Branch 12:</b> API method rules and authenticated Swagger.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final UserRepository userRepository;

        public SecurityConfig(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

    /**
     * Security filter chain.
     *
     * <p>Anonymous users may reach public pages (home, login, register, static assets).
     * {@code GET /api/**} is allowed for {@code USER} and {@code ADMIN}. Other API methods
     * require {@code ADMIN}. Swagger/OpenAPI require an authenticated user.
     * {@code /admin/**} requires {@code ADMIN}. Other requests need an authenticated session.
     * Form login success is handled by {@link #authenticationSuccessHandler()}.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public browser pages and static assets
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/error",
                                "/style.css",
                                "/index.html")
                        .permitAll()
                        // Branch 12: USER and ADMIN may read the API; writes are ADMIN-only
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/api/**").hasAuthority("ADMIN")
                        // Branch 12: Swagger/OpenAPI require a signed-in user
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**")
                        .authenticated()
                        // Admin pages require ADMIN authority.
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        // Everything else (including /change-password) needs a login
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        // GET /login is still rendered by LoginController → login.html
                        .loginPage("/login")
                        // POST /login is handled by the UsernamePasswordAuthenticationFilter
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                // Lab 4 Thymeleaf forms do not include CSRF tokens yet.
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * Database-backed {@link UserDetailsService} bean for Spring Security.
     *
     * <p><b>What this bean does:</b> given a username, it loads the matching Lab 4
     * {@link User} from PostgreSQL through {@link UserRepository} and converts it to
     * Spring Security {@link UserDetails} (username, password, authorities, enabled).
     *
     * <p><b>How it is wired:</b> this {@code @Bean} is detected by Spring Security and
     * registered with the {@code AuthenticationManager} (via
     * {@code DaoAuthenticationProvider}).
     * On form login, the filter calls {@code loadUserByUsername(...)}, then the
     * {@link PasswordEncoder} compares the submitted password to the stored value.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(this::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Lab 6 Branch 9: BCrypt password hashing.
     *
     * <p>{@link BCryptPasswordEncoder} hashes passwords on encode and verifies them on
     * login via {@code matches()}. Stored values must be BCrypt hashes — not plain text.
     * Existing Lab 4 rows need a one-time pgAdmin update to a known BCrypt hash before
     * those accounts can sign in.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Lab 6 Branch 7: post-login redirect based on {@code passwordChangeRequired}.
     *
     * <p>After Spring Security authenticates the user, this handler loads the Lab 4
     * {@link User} row and checks the flag. If true, the user is sent to
     * {@code /change-password} even if a saved request (for example {@code /admin/users})
     * would otherwise win. If false, behavior falls through to
     * {@link SavedRequestAwareAuthenticationSuccessHandler} (saved request or {@code /}).
     *
     * <p>This runs only after successful authentication — it is not a substitute for
     * requiring authentication on {@code /change-password} itself.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            {
                setDefaultTargetUrl("/");
                setAlwaysUseDefaultTargetUrl(false);
            }

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                String username = authentication.getName();
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                // Forced password change takes priority over any saved request URL.
                if (user.isPasswordChangeRequired()) {
                    getRedirectStrategy().sendRedirect(request, response, "/change-password");
                    return;
                }

                request.getSession(true).setAttribute("loginSuccessMessage", "Successfully signed in.");
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }

        /**
         * Maps a Lab 4 {@link User} entity to Spring Security {@link UserDetails}.
         *
         * <p>
         * Authority equals the stored role exactly ({@code USER} or {@code ADMIN}) — no
         * {@code ROLE_} prefix. Later authorization uses {@code hasAuthority("ADMIN")}.
         * The entity {@code enabled} flag becomes Spring's account-disabled status.
         */
        UserDetails toUserDetails(User user) {
                String authority = user.getRole();

                return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                                .password(user.getPassword())
                                .authorities(authority)
                                .disabled(!user.isEnabled())
                                .build();
        }
}
