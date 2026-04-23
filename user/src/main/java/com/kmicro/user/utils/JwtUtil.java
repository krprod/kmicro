package com.kmicro.user.utils;

import com.kmicro.user.exception.JWTFailureException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtil {
    // IMPORTANT: Replace this with a robust key loaded from application.properties
    // For demonstration, we generate a 256-bit key.
//    private final Key SECRET_KEY = secretKey();
    private final long EXPIRATION_TIME = 1000 * 60*60 ; // 1 hours
//    private static final DateTimeFormatter FORMATTER =  java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");


//    @Value("${auth.jwt.secret}")
//    private String SECRET_KEY;

    private final Key secretKey;

    JwtUtil( @Value("${auth.jwt.secret}") String SECRET_KEY){
//        System.out.println("Secret Key: " + SECRET_KEY);
        this.secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String useremail) {
        Map<String, Object> claims = new HashMap<>();

        // Add roles or custom claims if needed
//        claims.put("createdAt", new Date());
        claims.put("rememberMe", false);
        claims.put("usertype","Temp");
        return createToken(claims,useremail);
    }

    public String generateToken(Authentication authResponse){
        Map<String, Object> claims = new HashMap<>();
        claims.put("rememberMe", false);
//        claims.put("usertype","Temp");
//        authResponse.getAuthorities().stream().map(role->role).collect(Collectors.toSet());
        claims.put("roles",authResponse.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        return createToken(claims,authResponse.getName());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        log.info("Token created for user: {}",subject);
        return Jwts.builder()
//                .setIssuer("Eazy Bank")
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUseremail(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.trim());
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            log.info("Invalid JWT signature trace: {}", e);
            throw new JWTFailureException("Invalid JWT signature.");
//            log.trace("Invalid JWT signature trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.info("Expired JWT token trace: {}", e);
//            log.trace("Expired JWT token trace: {}", e);
            throw new JWTFailureException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
            throw new JWTFailureException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
            throw new JWTFailureException("JWT token compact of handler are invalid.");
        }
//        return false;
    }

    public User extractUserFromToken(String token){
        Claims claims = extractAllClaims(token);
        List<String> roles = (List<String>)claims.get("roles");
        List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new User(claims.getSubject(),token, authorities);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // --- Claim Extraction ---
    public String extractUseremail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public  Claims getClaims(String token){
        return extractAllClaims(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}
