package com.kmicro.apiGateway.routes;

import com.kmicro.apiGateway.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class OrderRouteConfig {

    @Value("${ORDER_PORT}")
    private String port;

    @Value("${ORDER_HOST:localhost}")
    private String orderHost;

    @Bean
    public RouteLocator orderRoutes(RouteLocatorBuilder builder, AuthenticationFilter authFilter) {
        String orderUri = "http://" + orderHost + ":" + port;
        return builder.routes()

                // 1. PUBLIC ROUTES (No Auth Filter)
/*                .route("product-public-route", r -> r
                        .path("/api/products/", "/api/products/paginated", "/api/products/qty-check", "/api/products/{id}")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST) // qty-check is POST but public
                        .filters(f -> f) // No filter applied here
                        .uri(productUri))*/

                // 2. SECURED ROUTES (Auth Filter Applied)
                .route("order-secured-route", r -> r
                        .path("/api/orders/**", "/api/order-service/logs", "/api/carts/**")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.PUT,HttpMethod.DELETE, HttpMethod.GET)
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(orderUri))

                // Documentation Routes
                .route("order-docs",
                        r -> r.path("/order-service/v3/api-docs/**", "/order-service/swagger-ui/**", "/order-service/webjars/**", "/order-service/springwolf/**")
                                .filters(f -> f.rewritePath("/order-service/(?<segment>.*)", "/${segment}"))
                                .uri(orderUri))
                .build();
    }

}//EC
