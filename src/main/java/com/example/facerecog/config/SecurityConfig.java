package com.example.facerecog.config;

import com.example.facerecog.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthSuccessHandler customAuthSuccessHandler;
    private final PasswordChangeFilter passwordChangeFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomAuthSuccessHandler customAuthSuccessHandler, PasswordChangeFilter passwordChangeFilter) {
        this.userDetailsService = userDetailsService;
        this.customAuthSuccessHandler = customAuthSuccessHandler;
        this.passwordChangeFilter = passwordChangeFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(passwordChangeFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/doRegister", "/css/**", "/js/**", "/error/403", "/change-password").permitAll()
                        .requestMatchers("/master/**").hasRole("MASTER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MASTER")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN", "MASTER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(e -> e.accessDeniedPage("/error/403"));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, PasswordEncoder passwordEncoder
    ) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
