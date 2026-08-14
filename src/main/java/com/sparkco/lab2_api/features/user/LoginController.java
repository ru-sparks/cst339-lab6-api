package com.sparkco.lab2_api.features.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the Thymeleaf login page.
 *
 * <p>Lab 5 Branch 3: only GET remains here. POST {@code /login} is processed by Spring
 * Security's form-login filter (see {@link SecurityConfig}), which authenticates through
 * {@link org.springframework.security.core.userdetails.UserDetailsService}. Do not restore
 * manual password comparison in this controller.
 */
@Controller
public class LoginController {

    /**
     * Renders {@code login.html}. Spring Security's {@code formLogin().loginPage("/login")}
     * redirects unauthenticated users here once route protection is enabled in later branches.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
