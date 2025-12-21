package com.kmicro.user.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenBlackListing {

    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";


    private final RedisTemplate<String, Object> redisTemplate; // Redis Injection
    private  final JwtUtil jwtUtil;

    // Using the same secret key as JwtUtil (ideally, both should load from properties)
    private final Key SECRET_KEY = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    public void blacklistToken(String token) {
        try {

            Claims claims = jwtUtil.getClaims(token);
            long expirationTimeMillis = claims.getExpiration().getTime();
            long currentTimeMillis = System.currentTimeMillis();

            if (expirationTimeMillis > currentTimeMillis) {
                // Calculate remaining TTL
                long ttlMillis = expirationTimeMillis - currentTimeMillis;

                // Store token in Redis with the calculated TTL
                String redisKey = BLACKLIST_KEY_PREFIX + token;
                // Using a simple boolean value to minimize storage overhead
                redisTemplate.opsForValue().set(redisKey, true, ttlMillis, TimeUnit.MILLISECONDS);

                log.info("Token blacklisted in for user: {} Remaining TTL (ms): {} ",claims.getSubject(), ttlMillis);
            } else {
                log.info("Token already expired for user: {}, no need to blacklist.", claims.getSubject());
            }

        } catch (Exception e) {
            // Log if token is malformed or already expired
            log.error("Failed to process or blacklist token: " + e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        String redisKey = BLACKLIST_KEY_PREFIX + token;
        // Check for key existence in Redis. Returns false if the key is expired or never existed.
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

}//EC
