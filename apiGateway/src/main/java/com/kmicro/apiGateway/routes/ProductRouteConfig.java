package com.kmicro.apiGateway.routes;

import com.kmicro.apiGateway.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class ProductRouteConfig {

    @Value("${PRODUCT_PORT}")
    private String port;

    // Inject the new HOST variable
    @Value("${PRODUCT_HOST:localhost}")
    private String productHost;

    @Bean
    public RouteLocator productRoutes(RouteLocatorBuilder builder, AuthenticationFilter authFilter) {
        String productUri = "http://" + productHost + ":" + port;
        return builder.routes()

                // 1. PUBLIC ROUTES (No Auth Filter)
                .route("product-public-route", r -> r
                        .path("/api/products/", "/api/products/paginated", "/api/products/qty-check", "/api/products/{id}")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST) // qty-check is POST but public
                        .filters(f -> f) // No filter applied here
                        .uri(productUri))

                // 2. SECURED ROUTES (Auth Filter Applied)
                .route("product-secured-route", r -> r
                        .path("/api/products/add", "/api/products/update", "/api/products/delete/**", "/api/product-service/logs",
                                "/api/products/bulk-update","/api/category/**")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.GET)
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(productUri))

                // Documentation Routes
                .route("product-docs",
                        r -> r.path("/product-service/v3/api-docs/**", "/product-service/swagger-ui/**","/product-service/webjars/**")
                        .filters(f -> f.rewritePath("/product-service/(?<segment>.*)", "/${segment}"))
                        .uri(productUri))
                .build();
    }
}
