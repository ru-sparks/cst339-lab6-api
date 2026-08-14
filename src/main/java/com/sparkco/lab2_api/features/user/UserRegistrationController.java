package com.sparkco.lab2_api.features.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Public self-registration. Lab 5 Branch 11 always assigns {@code USER}; administrators
 * promote accounts through {@code /admin/users}, not through this form.
 */
@Controller
public class UserRegistrationController {

    private final UserService userService;

    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO("", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDTO registration,
            RedirectAttributes redirectAttributes) {
        if (registration.username() == null || registration.username().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Username is required.");
            return "redirect:/register";
        }
        if (registration.password() == null || registration.password().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Password is required.");
            return "redirect:/register";
        }
        if (!registration.password().equals(registration.confirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/register";
        }
        if (userService.usernameExists(registration.username())) {
            redirectAttributes.addFlashAttribute("error", "Username is already taken.");
            return "redirect:/register";
        }

        User user = new User();
        user.setUsername(registration.username());
        user.setPassword(registration.password());
        user.setEnabled(true);
        user.setPasswordChangeRequired(true);
        // Branch 11: never trust a client-supplied role — self-registration is USER only.
        user.setRole("USER");

        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("success", "Registration complete. Please sign in.");
        return "redirect:/login";
    }
}
