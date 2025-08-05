package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        // return "Hello World! Bob";
        return """
                <html>
                  <body style='text-align:center; font-family:Arial;'>
                    <h1 style='color:green'>✅ Hello RTX Team</h1>
                    <img src='/images/logo.png' width='200'/>
                    <p>We are on Azure</p>
                  </body>
                </html>
                """;
    }
}
