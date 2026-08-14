package com.sparkco.lab2_api.features.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

class ChangePasswordControllerTest {

    @Test
    void changePassword_updatesAuthenticatedUserAndRedirectsToLogin() {
        UserService userService = mock(UserService.class);
        ChangePasswordController controller = new ChangePasswordController(userService);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        Principal principal = () -> "alice";

        given(userService.changePassword(eq("alice"), anyString())).willReturn(new User());

        String view = controller.changePassword(
                new ChangePasswordForm("secret123", "secret123"),
                principal,
                redirectAttributes);

        assertEquals("redirect:/login", view);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("success"));
        verify(userService).changePassword("alice", "secret123");
    }

    @Test
    void changePassword_returnsToFormWhenPasswordsDoNotMatch() {
        UserService userService = mock(UserService.class);
        ChangePasswordController controller = new ChangePasswordController(userService);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        Principal principal = () -> "alice";

        String view = controller.changePassword(
                new ChangePasswordForm("secret123", "different"),
                principal,
                redirectAttributes);

        assertEquals("redirect:/change-password", view);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("error"));
    }
}
