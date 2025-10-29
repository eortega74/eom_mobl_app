package com.example.demo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


//For logout
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    //comment this out for testing
    // @Bean
    // public LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
    //     OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler = 
    //     new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
    //     logoutSuccessHandler.setPostLogoutRedirectUri("https://keycloak.local:5001"); // your login or homepage URL
    //     return logoutSuccessHandler;
    // }

    //add these 2 back later: OAuth2AuthorizationRequestResolver resolver, LogoutSuccessHandler oidcLogoutSuccessHandler
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http
        
    ) throws Exception {
        return http
            // .csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .authorizeHttpRequests(auth -> auth
                //.requestMatchers("/", "/login", "/normal", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().permitAll()
                //.authenticated()
            )
            // .oauth2Login(oauth2 -> oauth2
            //     .loginPage("/login")
            //     .defaultSuccessUrl("/home", true)
            //     .authorizationEndpoint(authz -> authz
            //         .authorizationRequestResolver(resolver)
            //     )
            // )
            // .logout(logout -> logout 
            //     .logoutSuccessHandler(oidcLogoutSuccessHandler)
            //     .invalidateHttpSession(true)
            //     .clearAuthentication(true)
            //     .deleteCookies("JSESSIONID")
            // )
            // .formLogin().disable()
            .build();
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http,  HandlerMappingIntrospector introspector, ServerOAuth2AuthorizationRequestResolver resolver) throws Exception {
    //     return http // Authorize requests
    //             .csrf(csrf -> csrf.disable())
    //             .cors(withDefaults()) // <- ADD THIS
    //             .authorizeHttpRequests(authorizeRequests ->
    //                     authorizeRequests
    //                             .requestMatchers("/", "/login", "/favicon.ico", "/static/**", "/css/**", "/js/**", "/images/**", "/error", "/oauth2/**", "/resources/**").permitAll()
    //                             .anyRequest().authenticated()
    //             ) //may delete this seciton later
    //             .oauth2Login(oauth2 -> oauth2
    //                 //.loginPage("/oauth2/authorization/duende")
    //                 .defaultSuccessUrl("/home", true)
    //                 .auth.authorizationRequestResolver(resolver)
    //                 // .failureHandler((request, response, exception) -> {
    //                 //     exception.printStackTrace(); // or log it
    //                 //     response.sendRedirect("/login?error");
    //                 // })
    //             ).formLogin().disable()
    //             // .oauth2Login(withDefaults())
    //             // .formLogin(form -> form
    //             //         .loginPage("/login") //where GET requests go
    //             //         .permitAll()
    //             //         .defaultSuccessUrl("/home", true) // Define the default success URL here

    //             // )
    //             // .logout(logout -> logout
    //             //     .logoutSuccessHandler(oidcLogoutSuccessHandler()))
    //             .build();
    // }

    //comment this out for testing ONLY
    // @Bean
    // public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository repo) {
    //     var defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    //     defaultResolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
    //     return defaultResolver;
    // }




    // @Bean
    // public ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
    //     OidcClientInitiatedServerLogoutSuccessHandler successHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
    //     successHandler.setPostLogoutRedirectUri("{baseUrl}/login");
    //     return successHandler;
    // }
}
