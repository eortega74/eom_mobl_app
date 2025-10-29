package com.example.demo;

import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;


@Controller
public class Controllers {
    // @GetMapping("/login")
    // @ResponseBody
    // public String testing() {
    //     return "loading keycloak...";
    // }

    @GetMapping("/")
    //@ResponseBody
    public String one(@AuthenticationPrincipal OidcUser user) {
        return "redirect:/login";
        //temporarily comment out everything
        //System.out.println("User details: " + user.toString());
        // System.out.println("User email: " + user.getEmail());
        // System.out.println("User name: " + user.getFullName());
        // System.out.println("User claims: " + user.getClaims());
        //String username = user.getPreferredUsername();
        //return "Welcome, ";
        //return "Welcome home!";
    }

    @GetMapping("/logout-success")
    @ResponseBody
    public String logout() {
        return "successful signoff";
    }

    // @GetMapping("/home")
    // @ResponseBody
    // public String two() {
    //     return "loading keycloak...";
    // }

    @GetMapping("/home")
    public String plz(@AuthenticationPrincipal OidcUser user, Model model) {
        try {
            System.out.println("Attributes: " + user.getAttributes());
            System.out.println("Claims: " + user.getClaims());
            model.addAttribute("user", user);
            return "home";
            //System.out.println("User details: " + user.toString());
            //return user.toString();
        } catch(Exception e) {
            return "index";
            //return "WELCOME!!!";
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

}
