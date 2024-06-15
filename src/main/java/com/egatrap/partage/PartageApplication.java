package com.egatrap.partage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PartageApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartageApplication.class, args);
    }

}
