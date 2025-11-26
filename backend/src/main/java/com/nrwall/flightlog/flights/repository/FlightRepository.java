package com.nrwall.flightlog.flights.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nrwall.flightlog.flights.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {

  List<Flight> findByUserIdOrderByDepartureTimeDesc(Long userId);

  @Query("SELECT f FROM Flight f WHERE f.userId = :userId AND f.departureTime BETWEEN :from AND :to ORDER BY f.departureTime DESC")
  List<Flight> findInRange(@Param("userId") Long userId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

  Optional<Flight> findByIdAndUserId(Long id, Long userId);
}
