package com.sparkco.lab2_api.features.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object message = session.getAttribute("loginSuccessMessage");
            if (message != null) {
                model.addAttribute("success", message);
                session.removeAttribute("loginSuccessMessage");
            }
        }

        return "index";
    }
}
