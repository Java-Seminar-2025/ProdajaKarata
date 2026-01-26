package com.football.ticketsale.config;

import com.football.ticketsale.integration.footballdata.FootballDataProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FootballDataProperties.class)
public class AppConfig {}
