package com.kmicro.apiGateway.routes;

import com.kmicro.apiGateway.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class UserRouteConfig {

    @Value("${USER_PORT}")
    private String port;

    @Value("${USER_HOST:localhost}")
    private String userHost;

    @Bean
    public RouteLocator userRoutes(RouteLocatorBuilder builder, AuthenticationFilter authFilter) {
        String userUri = "http://" + userHost + ":" + port;
        return builder.routes()

                // 1. PUBLIC ROUTES (No Auth Filter)
                .route("user-public-route", r -> r
                        .path("/api/users/**", "/api/auth/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST) // qty-check is POST but public
                        .filters(f -> f) // No filter applied here
                        .uri(userUri))

                // 2. SECURED ROUTES (Auth Filter Applied)
                .route("user-secured-route", r -> r
                        .path("/api/users/address/**", "/api/user-service/logs", "/api/users/admin/**","/api/users/{id}")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.PUT,HttpMethod.DELETE, HttpMethod.GET)
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(userUri))

                // Documentation Routes
                .route("user-docs",
                        r -> r.path("/user-service/v3/api-docs/**", "/user-service/swagger-ui/**", "/user-service/webjars/**", "/user-service/springwolf/**")
                                .filters(f -> f.rewritePath("/user-service/(?<segment>.*)", "/${segment}"))
                                .uri(userUri))
                .build();
    }

}//EC
