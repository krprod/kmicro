package com.kmicro.notification.kafka.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentsListner {

//    @KafkaListener(
//            containerFactory = "notificationKafkaListenerContainerFactory",
//            topics = "t-payment-events",
//            groupId = "notification-service-group"
//    )
    public void listener(ConsumerRecord<String, String> requestRecord, @Header("eventType") String eventType, Acknowledgment ack) {
        log.info("Payment Event Received. EventType: {}, Key: {}, Partition: {}",eventType, requestRecord.key(), requestRecord.partition());
        try {
            // 1. Process Business Logic (e.g., Sending Notification)
            // Recommendation: Map to a specific DTO for cleaner code
//            processNotification(record.value());

            // 2. Commit manually only AFTER success

            // This ensures if the app crashes before this line, Kafka will redeliver
            ack.acknowledge();
        } catch (Exception  e) {
            log.error("Payment Processing Failed. Not acknowledging so message stays in Kafka.");
            log.error("Failed Record: {}", requestRecord.value());
            log.debug("Exception Occurred: ",e);
            // DO NOT acknowledge here.
            // Depending on your ErrorHandler, the message will either retry or move to DLT.
            // This triggers the DefaultErrorHandler and moves the message to the DLT.
            throw new RuntimeException("Deserialization failed, routing to DLT", e);
        }
    }
}//EC
