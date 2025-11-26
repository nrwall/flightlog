package com.nrwall.flightlog.flights.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nrwall.flightlog.flights.entity.Flight;

@Service
public class EnrichmentService {

  private static final Map<String, String> AIRLINE_MAP = Map.of(
    "AA", "American Airlines",
    "UA", "United Airlines",
    "DL", "Delta Air Lines",
    "WN", "Southwest Airlines",
    "BA", "British Airways",
    "AF", "Air France",
    "LH", "Lufthansa",
    "F9", "Frontier",
    "NK", "Spirit",
    "LA", "LATAM"
  );

  public void enrich(Flight f) {
    // Airline from flight number prefix
    String fn = f.getFlightNumber();
    if (fn != null && fn.length() >= 2) {
      String prefix = fn.substring(0, 2).toUpperCase();
      f.setAirline(AIRLINE_MAP.getOrDefault(prefix, prefix));
    }

    // Duration
    LocalDateTime dep = f.getDepartureTime();
    LocalDateTime arr = f.getArrivalTime();
    if (dep != null && arr != null && arr.isAfter(dep)) {
      long mins = Duration.between(dep, arr).toMinutes();
      f.setDurationMinutes((int) mins);
      // Red-eye if dep late night and arrives next morning
      int h = dep.getHour();
      boolean overnight = arr.toLocalDate().isAfter(dep.toLocalDate());
      f.setRedEye(overnight && (h >= 21 || h <= 5));
    }

    // Route
    if (f.getOrigin() != null && f.getDestination() != null) {
      f.setRoute(f.getOrigin().toUpperCase() + "→" + f.getDestination().toUpperCase());
    }
  }
}
