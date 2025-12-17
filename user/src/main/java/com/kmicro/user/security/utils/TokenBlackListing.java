package com.kmicro.user.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlackListing {

    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // Redis Injection

    @Autowired
    JwtUtil jwtUtil;

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

                System.out.println("Token blacklisted in Redis. Remaining TTL (ms): " + ttlMillis);
            } else {
                System.err.println("Token already expired, no need to blacklist.");
            }

        } catch (Exception e) {
            // Log if token is malformed or already expired
            System.err.println("Failed to process or blacklist token: " + e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        String redisKey = BLACKLIST_KEY_PREFIX + token;
        // Check for key existence in Redis. Returns false if the key is expired or never existed.
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

}//EC
