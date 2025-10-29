package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
// SECURITY IMPORTS COMMENTED OUT - NOT AVAILABLE IN CACHE
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.oauth2.core.oidc.user.OidcUser;
// import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
// import org.springframework.ui.Model;


@Controller
public class Controllers {
    
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Hello World! EOM QA Simple Application is running successfully!";
    }
    
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK - Application is healthy";
    }
    
    // ALL COMPLEX ENDPOINTS COMMENTED OUT - DEPENDENCIES NOT AVAILABLE IN CACHE
    /*
    @GetMapping("/logout-success")
    @ResponseBody
    public String logout() {
        return "successful signoff";
    }

    @GetMapping("/home")
    public String plz(@AuthenticationPrincipal OidcUser user, Model model) {
        try {
            System.out.println("Attributes: " + user.getAttributes());
            System.out.println("Claims: " + user.getClaims());
            model.addAttribute("user", user);
            return "home";
        } catch(Exception e) {
            return "index";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/normal")
    @ResponseBody
    public String normal() {
        return "normal route, sign in with duende for auth";
    }
    */

}
