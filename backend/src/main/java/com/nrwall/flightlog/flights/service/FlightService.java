package com.nrwall.flightlog.flights.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nrwall.flightlog.flights.entity.Flight;
import com.nrwall.flightlog.flights.repository.FlightRepository;

@Service
public class FlightService {

  @Autowired private FlightRepository repo;
  @Autowired private EnrichmentService enrichment;

  public List<Flight> listForUser(Long userId) {
    return repo.findByUserIdOrderByDepartureTimeDesc(userId);
  }

  public List<Flight> listForUser(Long userId, LocalDate from, LocalDate to) {
    LocalDateTime f = from.atStartOfDay();
    LocalDateTime t = to.atTime(LocalTime.MAX);
    return repo.findInRange(userId, f, t);
  }

  public Optional<Flight> getOwned(Long id, Long userId) {
    return repo.findByIdAndUserId(id, userId);
  }

  @Transactional
  public Flight create(Long userId, Flight f) {
    f.setUserId(userId);
    enrichment.enrich(f);
    return repo.save(f);
  }

  @Transactional
  public void deleteOwned(Long id, Long userId) {
    repo.findByIdAndUserId(id, userId).ifPresent(repo::delete);
  }
}
