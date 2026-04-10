package com.kmicro.order.utils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class DynamicEventCreator {

/*    private String eventType;
    private String sendto;
    private String subject;*/

    // This captures the dynamic "body" or any other unexpected fields
    private Map<String, Object> payload = new HashMap<>();

    @JsonAnySetter
    public void add(String key, Object value) {
        payload.put(key, value);
    }

    @JsonAnySetter
    public DynamicEventCreator addFluently(String key, Object value) {
        payload.put(key, value);
        return this; // Allows Method Chaining
    }
    public void clean() {
        payload.clear();
    }

    @JsonAnyGetter
    public Map<String, Object> getPayload() {
        return payload;
    }

}//EC