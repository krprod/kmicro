package com.kmicro.apiGateway.routes;

import com.kmicro.apiGateway.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class NotificationRouteConfig {

    @Value("${NOTIFICATION_PORT}")
    private String port;

    @Value("${NOTIFICATION_HOST:localhost}")
    private String notificationHost;

    @Bean
    public RouteLocator notificationRoutes(RouteLocatorBuilder builder, AuthenticationFilter authFilter) {
        String notificationUri = "http://" + notificationHost + ":" + port;
        return builder.routes()

                // 1. PUBLIC ROUTES (No Auth Filter)
/*                .route("product-public-route", r -> r
                        .path("/api/products/", "/api/products/paginated", "/api/products/qty-check", "/api/products/{id}")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST) // qty-check is POST but public
                        .filters(f -> f) // No filter applied here
                        .uri(productUri))*/

                // 2. SECURED ROUTES (Auth Filter Applied)
                .route("notification-secured-route", r -> r
                        .path("/api/notifications/**", "/api/notification-service/logs", "/api/emails/**")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.PUT,HttpMethod.GET)
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(notificationUri))

                // Documentation Routes
                .route("notification-docs",
                        r -> r.path("/notification-service/v3/api-docs/**", "/notification-service/swagger-ui/**", "/notification-service/webjars/**", "/notification-service/springwolf/**")
                                .filters(f -> f.rewritePath("/notification-service/(?<segment>.*)", "/${segment}"))
                                .uri(notificationUri))
                .build();
    }

}//EC
