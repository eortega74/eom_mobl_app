package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

@Service
public class LdapAuthService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public boolean authenticate(String username, String password) {
        try {
            return ldapTemplate.authenticate("", "(cn=" + username + ")", password);
        } catch (Exception e) {
            System.out.println("LDAP authentication failed: " + e.getMessage());
            return false;
        }
    }
}
