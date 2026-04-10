package com.kmicro.notification.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class EmailPayload {
    private final String recipient;
    private final String eventType;

    @Builder.Default
    private final Map<String, Object> data = new HashMap<>();

    // Fluent helper to add data without creating a DTO
    public static class EmailPayloadBuilder {
        public EmailPayloadBuilder add(String key, Object value) {
            if (this.data$value == null) this.data$value = new HashMap<>();
            this.data$value.put(key, value);
            return this;
        }
    }

}//EC
