package com.vibemitra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class VibeMitraApplication {

    public static void main(String[] args) {
        SpringApplication.run(VibeMitraApplication.class, args);
    }
}
