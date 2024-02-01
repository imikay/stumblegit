package com.stumblegit.core.auth;

import com.stumblegit.core.dao.autogen.UserMapper;
import com.stumblegit.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        User user = userMapper.loadUserByUsername(authentication.getName());

        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        System.out.println(passwordEncoder.matches("123456", user.getPassword()));

        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        GrantedAuthority auth = new SimpleGrantedAuthority(user.getAuthorities());
        roles.add(auth);

        if (passwordEncoder.matches(password, user.getPassword())) {
            // use the credentials
            // and authenticate against the third-party system
            return new UsernamePasswordAuthenticationToken(name, password, roles);
        } else {
            throw new BadCredentialsException("Bad username or pas" +
                    "sword! Please try again");
        }
    }

    private boolean shouldAuthenticateAgainstThirdPartySystem() {
        return false;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}