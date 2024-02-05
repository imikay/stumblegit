package com.stumblegit.core.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OauthController {
    @RequestMapping("/info")
    public String github(@AuthenticationPrincipal OAuth2User principal, Authentication auth, HttpSession session) {
        String email = principal.getAttribute("login");
        String name = principal.getAttribute("html_url");

        return "User Info: " + name + " (" + email + ")";
    }
}
