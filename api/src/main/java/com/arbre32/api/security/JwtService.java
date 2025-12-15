package com.arbre32.api.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
@Service
public class JwtService {
  @Value("${security.jwt.secret}") private String secret;
  @Value("${security.jwt.expMinutes:180}") private long expMinutes;
  public String generate(UUID subject, String handle, String role){
    Instant now = Instant.now();
    return Jwts.builder()
      .setSubject(subject.toString())
      .claim("handle", handle)
      .claim("role", role)
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
      .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
      .compact();
  }

  public String generateToken(String handle, String role){
    Instant now = Instant.now();
    return Jwts.builder()
            .claim("handle", handle)
            .claim("role", role)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
            .compact();
  }
  public Jws<Claims> parse(String token){
    return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token);
  }
}
