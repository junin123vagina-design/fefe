package com.hotel.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails gerente = User.withUsername("gerente")
                .password(passwordEncoder.encode("senha123"))
                .roles("GERENTE") // role GERENTE
                .build();

        UserDetails recepcionista = User.withUsername("recepcao")
                .password(passwordEncoder.encode("recep123"))
                .roles("RECEPCIONISTA")
                .build();

        return new InMemoryUserDetailsManager(gerente, recepcionista);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/index.html"
                        ).permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/docs/instrucoes.html", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/docs/instrucoes.html")
                        .permitAll()
                );

        return http.build();
    }
}