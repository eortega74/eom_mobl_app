package com.example.demo.service;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class LdapLogger {
    public static void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter("/app/auth.log", true))) {
            String timestamp = LocalDateTime.now() + " | ";
            out.println(timestamp + message);
            System.out.println(timestamp + message); // Consola del contenedor
        } catch (Exception e) {
            System.err.println("❌ Error writing log: " + e.getMessage());
        }
    }
}
