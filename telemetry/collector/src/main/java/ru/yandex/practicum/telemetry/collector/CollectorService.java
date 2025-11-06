package ru.yandex.practicum.telemetry.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CollectorService {
    public static void main(String[] args) {
        SpringApplication.run(CollectorService.class, args);
    }
}