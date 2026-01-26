package com.football.ticketsale.initializer;



import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.football.ticketsale.service.MatchSyncService; // use the service your controller calls


@Component
@Profile("dev") // optional: only run in dev profile
public class DevDatabaseSeeder implements CommandLineRunner {


    private final MatchSyncService matchService;


    public DevDatabaseSeeder(MatchSyncService matchService) {
        this.matchService = matchService;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("Automatically populating database with matches...");
// Call the same logic your AdminSyncController calls
        matchService.syncUpcoming();
        System.out.println("Database population complete!");
    }
}
