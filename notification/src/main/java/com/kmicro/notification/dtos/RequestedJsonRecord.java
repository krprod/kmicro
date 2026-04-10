package com.kmicro.notification.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;

public record RequestedJsonRecord(
        @NonNull String sendTo,
        @NonNull  String subject,
        JsonNode body
) { }
