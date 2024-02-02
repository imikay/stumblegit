package com.stumblegit.core.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @GetMapping("/ertre")
    public String response() {
        SecurityContextHolder.clearContext();
        return "/test/index";
    }

    @GetMapping("/")
    public String second(Model model, Authentication authentication) {
        System.out.println(authentication);
        System.out.println("This is private " + (authentication != null ? authentication.getName() + " " + authentication.getCredentials() + " " + authentication.getAuthorities() : "anonymous"));
        assert authentication != null;
        model.addAttribute("currentUserName", authentication.getName());
        return "profile";
    }
}
