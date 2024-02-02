package com.stumblegit.core.controller;

import com.stumblegit.core.dao.autogen.UserMapper;
import com.stumblegit.core.model.LoginRequest;
import com.stumblegit.core.model.User;
import com.stumblegit.core.service.Impl.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecurityController {
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SecurityContextHolderStrategy securityContextHolderStrategy;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @Secured("ROLE_USER")
    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response, Errors errors) {
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getUsername(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        System.out.println("=====================================");
        System.out.println(encoder.matches("123456", " $2a$10$dT6paDz/tXpJ207VwlitpOTap.gCfYdQM11Mv7RBqYyAlFc5EdwD."));
        System.out.println("encoder:  " + encoder.encode("123456"));
        System.out.println("=====================================");

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }


    @PostMapping(value = "/register")
    public String createUser(LoginRequest loginRequest, Errors errors) {
        User user = userDetailsService.createUser(loginRequest.getUsername(), loginRequest.getPassword());

        return  "redirect:/login?" + user.getId();
    }

    @GetMapping("/logout")
    public String response() {
        return "/logout";
    }

    @PostMapping("/logout")
    public String doLogout() {
        SecurityContextHolder.clearContext();

        return "redirect: /login?logout=1";
    }
}
