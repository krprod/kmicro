package com.kmicro.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig  implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints
                .allowedOrigins( "http://localhost:5173") // Add your frontend origins
//                .allowedOriginPatterns( "*") // Add your frontend origins
//                .allowedOrigins( "*") // Add your frontend origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Specify allowed methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow credentials (if applicable)
    }
}
