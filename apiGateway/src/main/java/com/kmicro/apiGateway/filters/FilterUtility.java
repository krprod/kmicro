package com.kmicro.apiGateway.filters;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "kmicro-correlation-id";
    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;


    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID);
            return requestHeaderList.stream().findFirst().get();
        } else {
            return null;
        }
    }

    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

    public Mono<Boolean> isTokenBlacklisted(String token) {
        String redisKey = BLACKLIST_KEY_PREFIX + token;
        // Check for key existence in Redis. Returns false if the key is expired or never existed.
        return reactiveRedisTemplate.hasKey(redisKey)
                .defaultIfEmpty(false);
    }

}
