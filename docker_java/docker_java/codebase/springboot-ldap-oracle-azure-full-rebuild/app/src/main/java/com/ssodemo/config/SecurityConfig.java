package com.ssodemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.beans.factory.annotation.Value;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.ldap.urls}")
    private String ldapUrl;

    @Value("${spring.ldap.username}")
    private String managerDn;

    @Value("${spring.ldap.password}")
    private String managerPassword;

    @Value("${spring.ldap.base}")
    private String baseDn;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .ldapAuthentication()
            .userSearchBase("ou=users")
            .userSearchFilter("(cn={0})")
            .contextSource()
                .url("ldap://ldap:1389")
                .managerDn("cn=admin,dc=example,dc=com")
                .managerPassword("admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin().disable();
    }
}
