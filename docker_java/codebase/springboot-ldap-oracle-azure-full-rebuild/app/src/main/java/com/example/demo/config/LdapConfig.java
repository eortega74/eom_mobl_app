package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
@Configuration
public class LdapConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .ldapAuthentication()
            .userSearchBase("ou=users")
            .userSearchFilter("(cn={0})")
            .contextSource(contextSource());
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(System.getenv("LDAP_URL"));
        contextSource.setBase(System.getenv("LDAP_BASE_DN")); // ← ESTO ES LO QUE FALTABA
        contextSource.setUserDn(System.getenv("LDAP_USER_DN"));
        contextSource.setPassword(System.getenv("LDAP_PASSWORD"));
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/login").permitAll() // para el API si deseas usarlo
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .defaultSuccessUrl("/home", true)
                .permitAll();
    }
}
