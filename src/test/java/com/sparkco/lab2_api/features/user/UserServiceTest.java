package com.sparkco.lab2_api.features.user;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Lab 5 Branch 8/9: password updates target the named user and store a BCrypt hash.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void changePassword_updatesNamedUserAndClearsFlag() {
        User alice = new User();
        alice.setUsername("alice");
        alice.setPassword("old-password");
        alice.setPasswordChangeRequired(true);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(passwordEncoder.encode("new-password")).thenReturn("$2a$10$encoded-new-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.changePassword("alice", "new-password");

        assertEquals("$2a$10$encoded-new-password", updatedUser.getPassword());
        assertFalse(updatedUser.isPasswordChangeRequired());
        verify(passwordEncoder).encode("new-password");
        verify(userRepository).findByUsername("alice");
        verify(userRepository).save(alice);
    }

    @Test
    void registerUser_encodesPasswordBeforeSave() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword("plain-password");
        user.setRole("ADMIN");

        when(passwordEncoder.encode("plain-password")).thenReturn("$2a$10$encoded-plain-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(user);

        assertEquals("$2a$10$encoded-plain-password", saved.getPassword());
        assertEquals("USER", saved.getRole());
        verify(passwordEncoder).encode("plain-password");
        verify(userRepository).save(user);
    }
}
