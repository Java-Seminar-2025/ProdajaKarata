package com.football.ticketsale.initializer;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.football.ticketsale.service.MatchSyncService;


@Component
@Profile("dev") // optional: only run in dev
public class DevDatabaseSeeder implements CommandLineRunner {


    private final MatchSyncService matchService;


    public DevDatabaseSeeder(MatchSyncService matchService) {
        this.matchService = matchService;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("Automatically populating database with matches...");
        matchService.syncUpcoming(); // THIS IS WHAT curl does
    }
}
