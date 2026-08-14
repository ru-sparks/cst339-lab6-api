package com.sparkco.lab2_api.features.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

/**
 * Lab 6 Branch 11: self-registration always creates {@code USER}; role is not client-controlled.
 */
class UserRegistrationControllerTest {

    @Test
    void registerUser_redirectsToLoginPageOnSuccess() {
        UserService userService = mock(UserService.class);
        UserRegistrationController controller = new UserRegistrationController(userService);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        given(userService.usernameExists("newuser")).willReturn(false);
        given(userService.registerUser(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        String view = controller.registerUser(
                new UserRegistrationDTO("newuser", "secret", "secret"),
                redirectAttributes);

        assertEquals("redirect:/login", view);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
        assertEquals("Registration complete. Please sign in.", redirectAttributes.getFlashAttributes().get("success"));
        verify(userService).registerUser(argThat(user -> "USER".equals(user.getRole())
                && "newuser".equals(user.getUsername())));
    }

    @Test
    void registerUser_alwaysAssignsUserRole() {
        UserService userService = mock(UserService.class);
        UserRegistrationController controller = new UserRegistrationController(userService);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        given(userService.usernameExists("attacker")).willReturn(false);
        given(userService.registerUser(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // Extra form fields cannot elevate privilege — DTO has no role; server forces USER.
        controller.registerUser(
                new UserRegistrationDTO("attacker", "secret", "secret"),
                redirectAttributes);

        verify(userService).registerUser(argThat(user -> "USER".equals(user.getRole())));
    }

    @Test
    void registerUser_rejectsDuplicateUsername() {
        UserService userService = mock(UserService.class);
        UserRegistrationController controller = new UserRegistrationController(userService);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        given(userService.usernameExists("taken")).willReturn(true);

        String view = controller.registerUser(
                new UserRegistrationDTO("taken", "secret", "secret"),
                redirectAttributes);

        assertEquals("redirect:/register", view);
        assertEquals("Username is already taken.", redirectAttributes.getFlashAttributes().get("error"));
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void registerUser_rejectsPasswordMismatch() {
        UserService userService = mock(UserService.class);
        UserRegistrationController controller = new UserRegistrationController(userService);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.registerUser(
                new UserRegistrationDTO("newuser", "secret", "different"),
                redirectAttributes);

        assertEquals("redirect:/register", view);
        assertEquals("Passwords do not match.", redirectAttributes.getFlashAttributes().get("error"));
        verify(userService, never()).registerUser(any(User.class));
    }
}
