package com.sparkco.lab2_api.features.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Application user rules for registration, lookup, administration, and password updates.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Lab 5 Branch 9/11: persist a BCrypt hash and always store role {@code USER}
     * for the public registration path (promotion happens only via administration).
     */
    public User registerUser(User user) {
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Lab 5 Branch 8/9: update the password for the authenticated account only.
     *
     * <p>Looks up the user by {@code username} (from {@code Principal.getName()}), stores a
     * BCrypt hash of {@code newPassword}, and clears {@code passwordChangeRequired} so
     * Branch 7's success handler no longer forces {@code /change-password} on the next login.
     *
     * @param username the authenticated username
     * @param newPassword the new plain-text password to hash and store
     * @return the saved user
     */
    public User changePassword(String username, String newPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserRole(Integer userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    public User toggleUserEnabled(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
}
