package com.kmicro.apiGateway.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    @Value("${auth.jwt.secret}")
    private String SECRET_KEY;

    @Autowired
    FilterUtility filterUtility;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Token"));
            }

            String token = authHeader.substring(7).trim();

            // 1. Reactive Check for Blacklist
            return filterUtility.isTokenBlacklisted(token)
                    .flatMap(isBlacklisted -> {
                        if (isBlacklisted) {
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Blacklisted"));
                        }

                        try {
                            // 2. Validate JWT
                            Claims claims = Jwts.parserBuilder()
                                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                                    .build()
                                    .parseClaimsJws(token)
                                    .getBody();

                            // 3. Extract Roles
                            List<?> rawRoles = claims.get("roles", List.class);
                            String roles = (rawRoles == null) ? "" : rawRoles.stream()
                                                                     .map(Object::toString)
                                                                     .map(String::toUpperCase)
                                                                     .collect(Collectors.joining(","));

                            log.info("Authenticated User: {} with roles: {}", claims.getSubject(), roles);

                            // 4. Mutate Request and Continue
                            return chain.filter(exchange.mutate()
                                    .request(r -> r
                                            .header("x-auth-user-id", claims.getSubject())
                                            .header("x-auth-user-roles", roles)
                                            .build())
                                    .build());

                        } catch (ExpiredJwtException e) {
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Expired"));
                        } catch (Exception e) {
                            log.error("JWT Validation Error: {}", e.getMessage());
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token"));
                        }
                    });
        };
    }


    /*@Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Get the token from header
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//            exchange.getRequest().getHeaders().asSingleValueMap().forEach((k, v) -> log.info("i-{}:i- {}", k, v));
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Token");
            }

            String token = authHeader.substring(7);

            if(filterUtility.isTokenBlacklisted(token)){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Blacklisted");
            }

            try {
                // 2. Validate JWT (using your shared Secret Key)
                log.info("SECRET_KEY: {}", SECRET_KEY);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token.trim())
                        .getBody();

                // 3. Optional: Pass user info to downstream services via headers
                List<String> userRoles  = claims.get("roles", ArrayList.class);
               String roles =  userRoles.stream().map(String::toUpperCase)
                        .collect(Collectors.joining(", "));
                log.info("ROLES: {}", roles);
                // 1. Mutate the request AND the exchange
                return chain.filter(
                        exchange.mutate()
                        .request(builder -> builder
                                .header("x-auth-user-id", claims.getSubject())
                                .header("x-auth-user-roles", roles)
                                .build())
                        .build()
                );
            } catch (ExpiredJwtException e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Expired");
            }
            catch (Exception e) {
                log.info("Exception: ", e);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token");
            }
        };
    }*/

   /* public boolean isTokenBlacklisted(String token) {
        String redisKey = BLACKLIST_KEY_PREFIX + token;
        // Check for key existence in Redis. Returns false if the key is expired or never existed.
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }*/
}
