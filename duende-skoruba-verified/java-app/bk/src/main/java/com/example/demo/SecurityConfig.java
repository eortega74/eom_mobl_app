// Spring Security OIDC config
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/").permitAll()  // público
                .anyRequest().authenticated()      // el resto requiere login
            )
            .oauth2Login()                         // habilita login con OIDC
            .and()
            .oauth2Client();                       // permite usar token OIDC con WebClient

        return http.build();
    }
}
