package com.football.ticketsale.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MatchSyncJob {

    private final MatchSyncService syncService;

    public MatchSyncJob(MatchSyncService syncService) {
        this.syncService = syncService;
    }

    // svaki dan u 3 ujutro
    @Scheduled(cron = "0 0 3 * * *")
    public void nightlySync() {
        syncService.syncUpcoming();
    }
}
