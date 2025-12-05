package com.kmicro.user.security.utils;

import com.kmicro.user.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    // IMPORTANT: Replace this with a robust key loaded from application.properties
    // For demonstration, we generate a 256-bit key.
//    private final Key SECRET_KEY = secretKey();
    private final long EXPIRATION_TIME = 1000 * 60*60 ; // 10 hours
//    private static final DateTimeFormatter FORMATTER =  java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");
    private final Environment env;

    private Key secretKey(){
        String secret = null;
        if(null != env){
            secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                    ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String useremail) {
        Map<String, Object> claims = new HashMap<>();

        // Add roles or custom claims if needed
//        claims.put("createdAt", new Date());
        claims.put("rememberMe", false);
        claims.put("usertype","Temp");
        return createToken(claims,useremail);
    }

    private String createToken(Map<String, Object> claims, String subject) {
//        System.out.println(ZoneId.systemDefault());
//
//        Instant currentTimeInMillis= Instant.ofEpochMilli(System.currentTimeMillis());
//        Instant expiryTimeInMillis = Instant.ofEpochMilli(System.currentTimeMillis() + EXPIRATION_TIME);
//
//        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentTimeInMillis, ZoneId.systemDefault());
//        LocalDateTime expiryDateTime = LocalDateTime.ofInstant(expiryTimeInMillis, ZoneId.systemDefault());

        return Jwts.builder()
//                .setIssuer("Eazy Bank")
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Token Validation ---

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUseremail(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
        return Jwts.parserBuilder().setSigningKey(secretKey()).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}
