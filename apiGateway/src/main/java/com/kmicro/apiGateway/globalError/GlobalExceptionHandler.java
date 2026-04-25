package com.kmicro.apiGateway.globalError;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-1) // Must be higher priority than the default handler
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties webProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(codecConfigurer.getWriters());
        this.setMessageReaders(codecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        int status = (int) errorPropertiesMap.get("status");

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }
}