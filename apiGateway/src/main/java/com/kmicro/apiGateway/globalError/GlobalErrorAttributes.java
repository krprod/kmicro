package com.kmicro.apiGateway.globalError;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        Map<String, Object> map = new HashMap<>();

        // Determine the status
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (error instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
        }

        // 3. Extract StackTrace as String
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        String stackTrace = sw.toString();

        // 4. LOG THE ERROR (This prints it to your console/file)
        log.error("Gateway Error: [Path: {}] [Status: {}] [Message: {}] \nTrace: {}",
                request.path(), status.value(), error.getMessage(), stackTrace);

        map.put("status", status.value());
        map.put("message", error.getMessage());
        map.put("path", request.path());
        map.put("timestamp", LocalDateTime.now());
        map.put("requestId", request.exchange().getRequest().getId());

        return map;
    }
}