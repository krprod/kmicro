package com.kmicro.notification.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(containerFactory = "notificationKafkaListenerContainerFactory", topics = "order-events", groupId = "notification-group")
    public void listen(ConsumerRecord<String, String> requestRecord) {
        log.info("Event Revieved In Notification:{}",requestRecord.value());
        try {
            JsonNode orderJson = objectMapper.readValue(requestRecord.value(), JsonNode.class);
        } catch (Exception e) {
            log.error("Error while processing notification message {}", e);
        }
    }
}
