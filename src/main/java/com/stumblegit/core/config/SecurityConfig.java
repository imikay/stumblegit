package com.stumblegit.core.config;

import com.stumblegit.core.auth.CustomAuthenticationProvider;
import com.stumblegit.core.auth.MySecurityContextHolderStrategy;
import com.stumblegit.core.service.Impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;

/**
 Just a POJO, no implementation of an interface, nor extends a class
 Just need to be specified as a @Configuration and @EnableWebSecurity
 and defined one or several @Bean, the central one is define a SecurityFilterChain bean
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig  {
    @Autowired
    CustomAuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/private").authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin((formLogin) -> {
                    formLogin
                            .usernameParameter("username")
                            .passwordParameter("password")
                            .loginPage("/login").permitAll()
                            .failureUrl("/login?failed")
                            .successHandler((request, response, authentication) -> {
                                System.out.println("Logged user: " + authentication.getName());
                                response.sendRedirect("/private");
                            })
                    ;
                })
                .logout((logout) -> logout
                        .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(COOKIES))))
                .sessionManagement((session) -> session
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                )
                .passwordManagement((management) -> management.changePasswordPage("/change-password"))
        ;

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityContextHolderStrategy holderStrategy() {
        return new MySecurityContextHolderStrategy();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Create two users
        return new UserDetailsServiceImpl();
    }
}
