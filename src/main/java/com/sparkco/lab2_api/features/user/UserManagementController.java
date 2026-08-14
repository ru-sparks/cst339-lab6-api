package com.sparkco.lab2_api.features.user;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String showUserManagementPage(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin-users";
    }

    @PostMapping("/admin/users/{userId}/role")
    public String updateUserRole(@PathVariable Integer userId, @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(userId, role);
            redirectAttributes.addFlashAttribute("success", "User role updated.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{userId}/toggle")
    public String toggleUserEnabled(@PathVariable Integer userId, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserEnabled(userId);
            redirectAttributes.addFlashAttribute("success", "User status updated.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{userId}/delete")
    public String deleteUser(@PathVariable Integer userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("success", "User deleted.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Unable to delete user.");
        }
        return "redirect:/admin/users";
    }
}
