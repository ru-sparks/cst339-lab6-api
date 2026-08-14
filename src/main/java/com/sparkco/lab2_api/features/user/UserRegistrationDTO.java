package com.sparkco.lab2_api.features.user;

/**
 * Lab 6 Branch 11: registration form fields only.
 * Role is never accepted from the browser — the server always assigns {@code USER}.
 */
public record UserRegistrationDTO(String username, String password, String confirmPassword) {
}
