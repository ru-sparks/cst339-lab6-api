package com.sparkco.lab2_api.features.user;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Thymeleaf change-password flow.
 *
 * <p>Lab 6 Branch 8: the POST handler uses {@link Principal} so only the signed-in user's
 * password is updated. {@code /change-password} remains authenticated via
 * {@link SecurityConfig} ({@code anyRequest().authenticated()}).
 */
@Controller
public class ChangePasswordController {

    private final UserService userService;

    public ChangePasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "change-password";
    }

    /**
     * Validates the form, then updates the authenticated user’s password.
     *
     * <p>{@code principal.getName()} is the username Spring Security placed in the session
     * at login. That value is passed to {@link UserService#changePassword(String, String)}.
     */
    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordForm") ChangePasswordForm form,
            Principal principal, RedirectAttributes redirectAttributes) {
        if (form.password() == null || form.password().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "A new password is required.");
            return "redirect:/change-password";
        }

        if (!form.password().equals(form.confirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/change-password";
        }

        userService.changePassword(principal.getName(), form.password());
        redirectAttributes.addFlashAttribute("success", "Password changed successfully. Please sign in.");
        return "redirect:/login";
    }
}
