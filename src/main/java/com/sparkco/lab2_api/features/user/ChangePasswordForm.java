package com.sparkco.lab2_api.features.user;

public record ChangePasswordForm(String password, String confirmPassword) {
    public ChangePasswordForm() {
        this("", "");
    }
}
