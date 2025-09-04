package com.art.artsea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(csrf -> csrf.disable()) //  fully disable CSRF
                .csrf(csrf -> csrf.disable()) //  fully disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() //  allow everything for now
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout
                        .logoutUrl("/logout")              // default logout URL
                        .logoutSuccessUrl("/")             // redirect here after logout
                        .invalidateHttpSession(true)       // invalidate session on logout
                        .deleteCookies("JSESSIONID")       // delete cookies if you want
                        .permitAll()
                );


        return http.build();
    }
}
