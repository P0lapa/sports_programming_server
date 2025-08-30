//package com.contest.sports_programming_server.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests
//                                .requestMatchers("/api/**").permitAll() // Разрешить все методы для /api/**
//                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Разрешить Swagger UI
//                                .anyRequest().authenticated()
//                );
////                .csrf().disable() // Отключить CSRF для упрощения тестирования
////                .formLogin().disable() // Отключить дефолтную форму логина
////                .httpBasic().disable(); // Отключить дефолтную HTTP Basic авторизацию
//        return http.build();
//    }
//}