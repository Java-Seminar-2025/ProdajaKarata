package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.repository.SeatReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SeatReservationDomainService {

    private final SeatReservationRepository seatReservationRepository;

    public SeatReservationDomainService(SeatReservationRepository seatReservationRepository) {
        this.seatReservationRepository = seatReservationRepository;
    }

    public long countTakenInRange(MatchEntity match, int start, int end, LocalDateTime now) {
        return seatReservationRepository.countTakenInRange(match, start, end, now);
    }

    public List<Integer> findTakenSeatNumbersInRange(MatchEntity match, int start, int end, LocalDateTime now) {
        return seatReservationRepository.findTakenSeatNumbersInRange(match, start, end, now);
    }

    public boolean existsByMatchAndSeatNumber(MatchEntity match, int seatNumber) {
        return seatReservationRepository.existsByMatchAndSeatNumber(match, seatNumber);
    }

    public boolean existsActiveSeatReservation(MatchEntity match, int seatNumber, LocalDateTime now) {
        return seatReservationRepository.existsActiveSeatReservation(match, seatNumber, now);
    }

    public Optional<SeatReservationEntity> findByTicket(TicketEntity ticket) {
        return seatReservationRepository.findByTicket(ticket);
    }

    @Transactional
    public SeatReservationEntity save(SeatReservationEntity sr) {
        return seatReservationRepository.save(sr);
    }

    @Transactional
    public void delete(SeatReservationEntity sr) {
        seatReservationRepository.delete(sr);
    }

    @Transactional
    public void deleteByTicket(TicketEntity ticket) {
        seatReservationRepository.deleteByTicket(ticket);
    }
}
