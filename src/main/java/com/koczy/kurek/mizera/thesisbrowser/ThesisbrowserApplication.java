package com.koczy.kurek.mizera.thesisbrowser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThesisbrowserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThesisbrowserApplication.class, args);
    }
}
