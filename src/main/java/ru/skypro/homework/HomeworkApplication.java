package ru.skypro.homework;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class of the app
 */
@SpringBootApplication
@OpenAPIDefinition
public class HomeworkApplication {
    /**
     * Starting point of the app
     * @param args command line values
     */
    public static void main(String[] args) {
        SpringApplication.run(HomeworkApplication.class, args);
    }

}
