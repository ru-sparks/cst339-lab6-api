package com.sparkco.lab2_api.features.user;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Lab 6 Branch 2: focused tests for DB-backed {@link UserDetails} mapping.
 */
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void toUserDetails_grantsAdminAuthorityWithoutRolePrefix() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("password");
        admin.setRole("ADMIN");
        admin.setEnabled(true);

        SecurityConfig securityConfig = new SecurityConfig(userRepository);
        UserDetails details = securityConfig.toUserDetails(admin);

        assertEquals("admin", details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN")));
        assertTrue(details.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(details.isEnabled());
    }

    @Test
    void toUserDetails_grantsUserAuthorityWithoutRolePrefix() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("secret");
        user.setRole("USER");
        user.setEnabled(true);

        SecurityConfig securityConfig = new SecurityConfig(userRepository);
        UserDetails details = securityConfig.toUserDetails(user);

        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("USER")));
        assertTrue(details.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void toUserDetails_mapsDisabledFlag() {
        User disabled = new User();
        disabled.setUsername("locked");
        disabled.setPassword("secret");
        disabled.setRole("USER");
        disabled.setEnabled(false);

        SecurityConfig securityConfig = new SecurityConfig(userRepository);
        UserDetails details = securityConfig.toUserDetails(disabled);

        assertFalse(details.isEnabled());
    }

    @Test
    void userDetailsService_loadsByUsername() {
        User alice = new User();
        alice.setUsername("alice");
        alice.setPassword("secret");
        alice.setRole("USER");
        alice.setEnabled(true);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));

        UserDetailsService userDetailsService = new SecurityConfig(userRepository).userDetailsService();
        UserDetails details = userDetailsService.loadUserByUsername("alice");

        assertEquals("alice", details.getUsername());
        assertEquals("secret", details.getPassword());
    }

    @Test
    void userDetailsService_throwsWhenUsernameMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = new SecurityConfig(userRepository).userDetailsService();

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing"));
    }
}
