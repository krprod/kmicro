package com.kmicro.order.kafka.listeners;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class orderDltHandler {

    @DltHandler
    public void processDlt(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // This is called ONLY after all 3 retries fail
        System.err.println("CRITICAL: Message in " + topic + " failed all retries. Payload: " + payload);
        // TODO: Save to 'failed_orders' DB table for customer support visibility
    }
}
