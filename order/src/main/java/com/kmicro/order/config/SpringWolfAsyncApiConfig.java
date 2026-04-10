package com.kmicro.order.config;

import io.github.springwolf.asyncapi.v3.model.info.Contact;
import io.github.springwolf.asyncapi.v3.model.info.Info;
import io.github.springwolf.asyncapi.v3.model.info.License;
import io.github.springwolf.asyncapi.v3.model.server.Server;
import io.github.springwolf.core.configuration.docket.AsyncApiDocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SpringWolfAsyncApiConfig {

    @Bean
    public AsyncApiDocket asyncApiDocket() {
        // 1. Metadata - Basic Project Info
        Info info = Info.builder()
                .title("E-Commerce Microservices - Messaging API")
                .version("2.0.0")
                .description("Documentation for Kafka-based event-driven communication")
                .contact(Contact.builder()
                        .name("Backend Architecture Team")
                        .url("https://github.com/your-org/ecommerce-project")
                        .email("arch-support@company.com")
                        .build())
                .license(License.builder()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                        .build())
                .build();

        // 2. Server Definition - Defining our Kafka Broker
        Server kafkaServer = Server.builder()
                .protocol("kafka")
                .host("kafka-broker.internal:9092")
                .description("Internal Development Kafka Cluster")
                .build();

        // 3. The Docket - The root of the AsyncAPI document
        return AsyncApiDocket.builder()
                .basePackage("com.yourcompany.ecommerce.messaging.listeners") // Scan package
                .info(info)
                .servers(Map.of("dev-kafka", kafkaServer)) // Supports multiple brokers
                .id("urn:com:yourcompany:ecommerce:order-service") // Unique AsyncAPI ID
                .build();
    }

}
