package com.example.shortform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ShortformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortformApplication.class, args);
    }

}
