package com.stumblegit.core.service.Impl;

import com.github.javafaker.Faker;
import com.stumblegit.core.dao.autogen.UserMapper;
import com.stumblegit.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
        User user = userMapper.loadUserByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Not found user " + username);
        }

        return convertToSecurityUser(user);
    }

    public org.springframework.security.core.userdetails.User convertToSecurityUser(User user) {
        return (org.springframework.security.core.userdetails.User) org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getAuthorities())
                .build();
    }

    public User createUser(String email, String password) {
        User user = new User();

        Faker faker = new Faker();

        String username = faker.random().toString();

        user.setUsername(username);
        user.setEmail(email);
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        userMapper.createUser(user);

        return user;
    }
}
