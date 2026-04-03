package com.kientran.cinehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CineHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineHubApplication.class, args);
    }

}
