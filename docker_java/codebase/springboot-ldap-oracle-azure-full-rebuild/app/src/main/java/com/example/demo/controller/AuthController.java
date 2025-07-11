package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        logger.info("🔐 Authenticating user: {}", username);

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://ldap:1389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + username + ",ou=users,dc=example,dc=com");
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext ctx = new InitialDirContext(env);
            logger.info("✅ LDAP bind successful for user: {}", username);
            ctx.close();
            return "✅ Auth success for user: " + username;
        } catch (AuthenticationException ae) {
            logger.error("❌ Authentication failed: {}", ae.getMessage());
            return "❌ Invalid credentials";
        } catch (Exception e) {
            logger.error("🚨 LDAP error: {}", e.getMessage(), e);
            return "❌ LDAP error: " + e.getMessage();
        }
    }
}
