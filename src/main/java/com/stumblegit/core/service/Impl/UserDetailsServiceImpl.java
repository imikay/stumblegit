package com.stumblegit.core.service.Impl;

import com.stumblegit.core.dao.autogen.UserMapper;
import com.stumblegit.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Not found user " + username);
        }

        return convertToSecurityUser(user);
    }

    public org.springframework.security.core.userdetails.User convertToSecurityUser(User user) {
        return (org.springframework.security.core.userdetails.User) org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getAuthorities())
                .build();
    }
}
