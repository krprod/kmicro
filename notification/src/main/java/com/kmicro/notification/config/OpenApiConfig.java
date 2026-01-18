package com.kmicro.notification.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API and Kafka Consumer & Producer")
                        .version("1.0")
                        .description("API for Managing Notifications, using RestEndpoint and Kafka Topics")
                        .contact(new Contact().name("Backend Team").email("dev@kmicro.com")))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("http://localhost:8096").description("Local Dev"));
    }
}
