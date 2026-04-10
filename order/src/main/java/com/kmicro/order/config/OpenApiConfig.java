package com.kmicro.order.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order and Cart Service API")
                        .version("1.0")
                        .description("API for managing User Carts and Orders")
                        .contact(new Contact().name("Backend Team").email("dev@kmicro.com")))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("http://localhost:8091").description("Local Dev"));
    }

//    @Bean
//    public Clock clock() {
//        // This forces the clock used by the app to always be UTC
//        return Clock.systemUTC();
//    }
}
