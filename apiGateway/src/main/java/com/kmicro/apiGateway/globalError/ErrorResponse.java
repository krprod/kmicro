package com.kmicro.apiGateway.globalError;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {}
