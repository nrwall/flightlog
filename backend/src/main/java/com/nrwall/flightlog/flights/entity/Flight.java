package com.nrwall.flightlog.flights.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "flight")
public class Flight {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false, length = 8)
  private String flightNumber;

  @Column(nullable = false, length = 8)
  private String origin;

  @Column(nullable = false, length = 8)
  private String destination;

  @Column(nullable = false)
  private LocalDateTime departureTime;

  @Column(nullable = false)
  private LocalDateTime arrivalTime;

  @Column(length = 64)
  private String airline; // inferred from flight number prefix

  private Integer durationMinutes; // computed

  private boolean isRedEye; // computed

  @Column(length = 32)
  private String seatClass;

  @Column(length = 256)
  private String route; // "SFO→JFK"

  @Column(length = 512)
  private String notes;

  public Flight() {}

  // getters and setters
  public Long getId() { return id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getFlightNumber() { return flightNumber; }
  public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
  public String getOrigin() { return origin; }
  public void setOrigin(String origin) { this.origin = origin; }
  public String getDestination() { return destination; }
  public void setDestination(String destination) { this.destination = destination; }
  public LocalDateTime getDepartureTime() { return departureTime; }
  public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
  public LocalDateTime getArrivalTime() { return arrivalTime; }
  public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
  public String getAirline() { return airline; }
  public void setAirline(String airline) { this.airline = airline; }
  public Integer getDurationMinutes() { return durationMinutes; }
  public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
  public boolean isRedEye() { return isRedEye; }
  public void setRedEye(boolean isRedEye) { this.isRedEye = isRedEye; }
  public String getSeatClass() { return seatClass; }
  public void setSeatClass(String seatClass) { this.seatClass = seatClass; }
  public String getRoute() { return route; }
  public void setRoute(String route) { this.route = route; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
}
