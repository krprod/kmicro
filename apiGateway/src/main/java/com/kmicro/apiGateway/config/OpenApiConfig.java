package com.kmicro.apiGateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;
import java.util.Set;


@Configuration
public class OpenApiConfig {

    private final SwaggerUiConfigProperties swaggerUiConfigProperties;

    public OpenApiConfig(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        this.swaggerUiConfigProperties = swaggerUiConfigProperties;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gateway")
                        .version("1.0")
                        .description("Gateway managing all service APIs")
                        .contact(new Contact().name("Backend Team").email("dev@kmicro.com")))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("http://localhost:9096").description("Gateway Dev Server"));
//                .addServersItem(new io.swagger.v3.oas.models.servers.Server().url("https://api.kmicro.com").description("Production"));
    }

    @PostConstruct
    public void init() {
        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new LinkedHashSet<>();
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl("Product Service", "/product-service/v3/api-docs", "Product Service"));
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl("Order Service", "/order-service/v3/api-docs", "Order Service"));
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl("User Service", "/user-service/v3/api-docs", "User Service"));
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl("Notification Service", "/notification-service/v3/api-docs", "Notification Service"));

        swaggerUiConfigProperties.setUrls(urls);
    }

}//EC
