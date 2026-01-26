package com.football.ticketsale.config;

import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.entity.StadiumSectionEntity;
import com.football.ticketsale.repository.StadiumRepository;
import com.football.ticketsale.repository.StadiumSectionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StadiumSectionSeeder implements CommandLineRunner {

    private final StadiumRepository stadiumRepository;
    private final StadiumSectionRepository sectionRepository;

    public StadiumSectionSeeder(StadiumRepository stadiumRepository, StadiumSectionRepository sectionRepository) {
        this.stadiumRepository = stadiumRepository;
        this.sectionRepository = sectionRepository;
    }

    @Override
    public void run(String... args) {
        List<StadiumEntity> stadiums = stadiumRepository.findAll();
        for (StadiumEntity stadium : stadiums) {
            if (!sectionRepository.findByStadiumOrderByStandNameAscSectionCodeAsc(stadium).isEmpty()) {
                continue;
            }

            int seats = stadium.getNumberOfSeats() == null ? 0 : stadium.getNumberOfSeats();
            if (seats < 90) continue;

            // moze se namjestati kako zelimo
            create(stadium, "Main Stand", "U1", 1, 200);
            create(stadium, "Main Stand", "U2", 201, 400);
            create(stadium, "Main Stand", "U3", 401, 600);

            create(stadium, "Upper", "L1", 601, 800);
            create(stadium, "Upper", "L2", 801, 1000);

            create(stadium, "Middle", "K1", 1001, 1200);
            create(stadium, "Middle", "K2", 1201, 1400);
        }
    }

    private void create(StadiumEntity stadium, String stand, String code, int start, int end) {
        int cap = stadium.getNumberOfSeats() == null ? 0 : stadium.getNumberOfSeats();
        if (start > cap) return;
        end = Math.min(end, cap);

        StadiumSectionEntity s = new StadiumSectionEntity();
        s.setStadium(stadium);
        s.setStandName(stand);
        s.setSectionCode(code);
        s.setSectionName(code);
        s.setSeatStart(start);
        s.setSeatEnd(end);
        sectionRepository.save(s);
    }

}
