package com.kmicro.apiGateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Optional;

@Configuration
public class RateLimiterConfig {

    @Bean(name = "userKeyResolver")
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName) // Check for JWT User
                .switchIfEmpty(Mono.justOrEmpty(getClientIp(exchange))) // Fallback to IP
                .defaultIfEmpty("anonymous-bucket"); // Safety fallback
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0];
        }
        return Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                .map(addr -> addr.getAddress().getHostAddress())
                .orElse(null);
    }

//    public KeyResolver userKeyResolver() {
//        return exchange -> exchange.getPrincipal()
//                // 1. Priority: Authenticated User (JWT 'sub' or 'name')
//                .map(Principal::getName)
//                .switchIfEmpty(Mono.defer(() -> {
//                    // 2. Fallback: Identify by IP (handling Load Balancers/Proxies)
//                    String ip = Optional.ofNullable(exchange.getRequest().getRemoteAddress())
//                            .map(InetSocketAddress::getAddress)
//                            .map(java.net.InetAddress::getHostAddress)
//                            .orElseGet(() -> {
//                                // Check X-Forwarded-For if behind Nginx/ALB
//                                String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
//                                return (forwarded != null) ? forwarded.split(",")[0] : null;
//                            });
//
//                    return Mono.justOrEmpty(ip);
//                }))
//                // 3. Ultimate Fallback: Never return Mono.empty()
//                // Returning empty allows the request to bypass the rate limiter entirely!
//                .defaultIfEmpty("anonymous-threat-actor");
//    }

/*    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 1. Try to get the IP address from the request
            String key = Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                    .map(InetSocketAddress::getAddress)
                    .map(java.net.InetAddress::getHostAddress)
                    // 2. Fallback: If IP is null (e.g., weird proxy), try a header
                    .orElseGet(() -> exchange.getRequest().getHeaders().getFirst("X-Forwarded-For"));

            // 3. Ultimate Fallback: Never return Mono.empty()!
            // Returning a static string ensures the request is still limited
            // under a "general" bucket rather than bypassing the limiter entirely.
            return Mono.just(key != null ? key : "unresolved-client-ip");
        };
    }*/

//    @Bean
//    public KeyResolver userKeyResolver() {
//        // Limits based on the "user" query parameter
//        // You could also use: exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
//        return exchange -> Mono.just(
//                exchange.getRequest().getQueryParams().getFirst("user") != null
//                        ? exchange.getRequest().getQueryParams().getFirst("user")
//                        : "anonymous"
//        );
//    }

}//EC
