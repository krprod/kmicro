package com.kmicro.product.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CommonConf {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }


//    --- ObservationFilter used for micrometer based tracing,
    /* it requires app.yml to have below tracing option
    * management:
              tracing:
                sampling:
                  probability: 1.0  # Capture 100% of traces for debugging
                otlp:
                  tracing:
                    endpoint: http://localhost:4317 # Ensure this matches your Tempo OTLP port
              otlp:
                metrics:
                  export:
                    url: http://localhost:4317
    * */
/*    @Bean
    ObservationFilter headerObservationFilter() {
        log.info("Observation Filter Bean---");
        return (context) -> {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            log.info("Observation Filter Bean attrs: {}", attrs);
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String userId = request.getHeader("x-auth-user-id");
                if (userId != null) {
                    // This adds it to the "Tags" section in Tempo
                    context.addLowCardinalityKeyValue(io.micrometer.common.KeyValue.of("http.header.x-auth-user-id", userId));
                }
            }
            log.info("Observation Filter Bean context: {}", context);
            return context;
        };
    }*/

  /*  @Bean
    ObservationFilter headerObservationFilter() {
        return (context) -> {
            // 1. Check if this is an HTTP server request observation
            if (context instanceof ServerRequestObservationContext serverContext) {
                HttpServletRequest request = serverContext.getCarrier();
                log.info("ObservationFilter request:");
                request.getHeaderNames().asIterator().forEachRemaining(System.out::println);
                if (request != null) {
                    String userId = request.getHeader("x-auth-user-id");

                    if (userId != null) {
                        // 2. Add as a low cardinality tag so it shows in Tempo
                        return context.addLowCardinalityKeyValue(KeyValue.of("http.header.x-auth-user-id", userId));
                    }
                }
            }
            return context;
        };
    }*/

    /*@Bean
    ObservationFilter headerObservationFilter() {
        return (context) -> {
            if (context instanceof ServerRequestObservationContext serverContext) {
                HttpServletRequest request = serverContext.getCarrier();
                // Use "unknown" or "none" if the header is missing
                String userId = (request != null && request.getHeader("x-auth-user-id") != null)
                        ? request.getHeader("x-auth-user-id")
                        : "anonymous";

                // This ensures the KEY always exists, satisfying Prometheus
                return context.addLowCardinalityKeyValue(KeyValue.of("http.header.x-auth-user-id", userId));
            }
            return context;
        };
    }*/

   /* @Bean
    ObservationFilter authHeaderObservationFilter() {
        return (context) -> {
            if (context instanceof ServerRequestObservationContext serverContext) {
                HttpServletRequest request = serverContext.getCarrier();
                if (request != null) {
                    String userId = request.getHeader("x-auth-user-id");
                    String roles = request.getHeader("x-auth-user-roles");
                    log.info("ObservationFilter request headers AUTH: {}", request.getHeader("x-auth-user-id"));
                    request.getHeaderNames().asIterator().forEachRemaining(System.out::println);
                    // User ID is High Cardinality (Unique per user) -> Use for Tracing/Tempo
                    if (userId != null) {
                        context.addHighCardinalityKeyValue(KeyValue.of("http.header.x-auth-user-id", userId));
                    }else {
                        context.addHighCardinalityKeyValue(KeyValue.of("http.header.x-auth-user-id", "anonymous"));
                    }
                    if (roles != null) {
                        context.addHighCardinalityKeyValue(KeyValue.of("http.header.x-auth-user-roles", roles));
                    }else {
                        context.addHighCardinalityKeyValue(KeyValue.of("http.header.x-auth-user-roles", "anonymous"));
                    }
                }
            }
            log.info("ObservationFilter context: {}", context);
            return context;
        };
    }*/

}//EC
