package com.nrwall.flightlog.flights.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.nrwall.flightlog.flights.entity.Flight;
import com.nrwall.flightlog.flights.service.FlightService;
import com.nrwall.flightlog.users.entity.AppUser;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

  @Autowired private FlightService service;

  public static record CreateFlightRequest(
    @NotBlank String flightNumber,
    @NotBlank String origin,
    @NotBlank String destination,
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalTime,
    String seatClass,
    String notes
  ) {}

  public static record FlightResponse(
    Long id, String flightNumber, String origin, String destination,
    String airline, Integer durationMinutes, boolean isRedEye, String seatClass,
    String route, String notes, LocalDateTime departureTime, LocalDateTime arrivalTime
  ) {
    public static FlightResponse of(Flight f) {
      return new FlightResponse(
        f.getId(), f.getFlightNumber(), f.getOrigin(), f.getDestination(),
        f.getAirline(), f.getDurationMinutes(), f.isRedEye(), f.getSeatClass(),
        f.getRoute(), f.getNotes(), f.getDepartureTime(), f.getArrivalTime()
      );
    }
  }

  @GetMapping
  public ResponseEntity<?> list(Authentication auth,
    @RequestParam(name = "from", required = false) String from,
    @RequestParam(name = "to", required = false) String to) {

    AppUser user = (AppUser) auth.getPrincipal();

    List<Flight> flights;
    if (from != null && to != null) {
      try {
        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);
        flights = service.listForUser(user.getId(), f, t);
      } catch (DateTimeParseException e) {
        return ResponseEntity.badRequest().body("from/to must be YYYY-MM-DD");
      }
    } else {
      flights = service.listForUser(user.getId());
    }
    return ResponseEntity.ok(flights.stream().map(FlightResponse::of).collect(Collectors.toList()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable("id") Long id, Authentication auth) {
    AppUser user = (AppUser) auth.getPrincipal();
    return service.getOwned(id, user.getId())
      .<ResponseEntity<?>>map(f -> ResponseEntity.ok(FlightResponse.of(f)))
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateFlightRequest req, Authentication auth) {
    AppUser user = (AppUser) auth.getPrincipal();
    Flight f = new Flight();
    f.setFlightNumber(req.flightNumber());
    f.setOrigin(req.origin());
    f.setDestination(req.destination());
    f.setDepartureTime(req.departureTime());
    f.setArrivalTime(req.arrivalTime());
    f.setSeatClass(req.seatClass());
    f.setNotes(req.notes());
    Flight saved = service.create(user.getId(), f);
    return ResponseEntity.ok(FlightResponse.of(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable("id") Long id, Authentication auth) {
    AppUser user = (AppUser) auth.getPrincipal();
    service.deleteOwned(id, user.getId());
    return ResponseEntity.noContent().build();
  }
}
