package com.kientran.cinehub;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CineHubApplication {

    public static void main(String[] args) {
        try {
            Dotenv.configure().load();
        } catch (io.github.cdimascio.dotenv.DotenvException e) {
            System.err.println("Warning: .env file not found or could not be loaded.");
        }
        SpringApplication.run(CineHubApplication.class, args);
    }
}