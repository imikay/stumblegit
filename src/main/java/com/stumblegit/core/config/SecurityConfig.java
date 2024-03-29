package com.stumblegit.core.config;

import com.stumblegit.core.auth.CustomAuthenticationProvider;
import com.stumblegit.core.auth.MySecurityContextHolderStrategy;
import com.stumblegit.core.service.Impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.ALL;
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
//                                .requestMatchers("/profile/", "/oauth2/info").authenticated()
                                .anyRequest().authenticated()
                )
                .formLogin((formLogin) -> {
                    formLogin
                            .usernameParameter("email")
                            .passwordParameter("password")
                            .loginPage("/login").permitAll()
                            .successHandler((request, response, authentication) -> {
                                response.sendRedirect("/profile/");
                            })
                    ;
                })
                .logout((logout) -> logout
                        // Note: This is not gonna work if you use http request, only works in https
                        // the header is only applied if the request is secure
                        .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ALL)))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .sessionManagement((session) -> session
                                .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(true) // Can cause couldn't login after logout
                )
                .passwordManagement((management) -> management.changePasswordPage("/change-password"))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .oauth2Login(oauth -> {
                    oauth.defaultSuccessUrl("/oauth2/info");
                })
        ;

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        // Only DelegationPasswordEncoder adding signature to the password
        DelegatingPasswordEncoder delegatingPasswordEncoder =
                (DelegatingPasswordEncoder) PasswordEncoderFactories
                        .createDelegatingPasswordEncoder();

        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder(10));

        return delegatingPasswordEncoder;
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
