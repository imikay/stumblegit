package com.stumblegit.core.controller;

import org.apache.catalina.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping("hello")
    public String home(@AuthenticationPrincipal Principal principal) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.getAuthentication();

        System.out.println(principal);
        return "Hello World! " + (principal != null ? ((User)principal).getUsername() : "anonymous");
    }
    @Secured("ROLE_USER")
    @GetMapping("private")
    public String second(Authentication authentication) {
        System.out.println(authentication);
        return "This is private " + (authentication != null ? authentication.getName() + " " + authentication.getCredentials() + " " + authentication.getAuthorities() : "anonymous");
    }
}
