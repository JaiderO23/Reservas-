package com.Reservas.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.Reservas.reservas",
        "controller",
        "service",
        "repository",
        "model",
        "config",
        "exception",
        "util"
})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
public class ReservasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasApplication.class, args);
    }

}