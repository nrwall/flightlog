package com.nrwall.flightlog.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.expiration-minutes}")
  private long expirationMinutes;

  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public String generateToken(String username, Long userId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("uid", userId);
    return createToken(claims, username);
  }

  public boolean isTokenValid(String token, String username) {
    String subj = extractUsername(token);
    return subj != null && subj.equals(username) && !isTokenExpired(token);
  }

  private String createToken(Map<String, Object> claims, String subject) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMinutes * 60 * 1000);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
