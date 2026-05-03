package com.engineering.library;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // activates createdAt / updatedAt auto-population
@OpenAPIDefinition(
    info = @Info(
        title        = "Engineering Library Management API",
        version      = "1.0.0",
        description  = "Comprehensive REST API for the Engineering School Library — book catalogue, member management, and borrowing workflows.",
        contact      = @Contact(name = "Library Admin Team", email = "library@engineering.edu"),
        license      = @License(name = "MIT")
    )
)
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }
}
