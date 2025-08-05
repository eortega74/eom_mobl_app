package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        // Colores ANSI
        String red = "\u001B[31m";
        String green = "\u001B[32m";
        String blue = "\u001B[34m";
        String reset = "\u001B[0m";

        System.out.println(green + "✅ Aplicación iniciada correctamente!" + reset);
        System.out.println(blue + "🌐 URL: http://localhost:8080" + reset);
        System.out.println(red + "❤️ Gracias por usar nuestra app!" + reset);
    }
}
