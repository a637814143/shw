package com.example.housekeeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HousekeepingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HousekeepingApplication.class, args);
    }

}
