package ru.develonica.load.testing.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LoadTestingRunner {
    public static void main(String[] args) {
        SpringApplication.run(LoadTestingRunner.class, args);
    }
}
