package com.football.ticketsale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FootballTicketSaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballTicketSaleApplication.class, args);
    }
}