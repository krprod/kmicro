package com.kmicro.apiGateway.routes;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class GlobalGatewayConfig {

    @Value("${frontURL}")
    private String frontendUrl;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(frontendUrl, "http://localhost:9096"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        corsConfig.setAllowCredentials(false);

        org.springframework.web.cors.reactive.CorsConfigurationSource source = exchange -> corsConfig;

        return new CorsWebFilter(source);
    }
}
