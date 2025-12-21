package com.kmicro.user.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0")
                        .description("API for managing users and authentication and authorization")
                        .contact(new Contact().name("Backend Team").email("dev@kmicro.com")))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("http://localhost:8085").description("Local Dev"));
//                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("https://api.kmicro.com").description("Production"));
    }
}
