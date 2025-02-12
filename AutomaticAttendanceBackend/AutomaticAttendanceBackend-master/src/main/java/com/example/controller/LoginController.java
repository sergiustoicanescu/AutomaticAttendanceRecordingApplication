package com.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        Object error = request.getSession().getAttribute("loginError");
        if (error != null) {
            model.addAttribute("loginError", error);
            request.getSession().removeAttribute("loginError");
        }
        return "login";
    }

}
