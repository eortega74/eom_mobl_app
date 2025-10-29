package com.eom.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SimpleEomQaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleEomQaApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Hello from EOM QA Simple Application!";
    }

    @RequestMapping("/api/hello")
    public ResponseEntity<Map<String, Object>> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from EOM QA API!");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "EOM QA Simple Application");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
}