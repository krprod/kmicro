package com.kmicro.notification.kafka.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.notification.kafka.processors.MailEventProcessor;
import com.kmicro.notification.kafka.schemas.OrderConfirmed;
import io.github.springwolf.core.asyncapi.annotations.AsyncListener;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrdersListener {

        private final MailEventProcessor mailEventProcessor;
        private final ObjectMapper objectMapper;

    @AsyncListener(operation = @AsyncOperation(
            channelName = "t-order-events",
            description = "Consumes inventory updates to sync local cache",
            payloadType = OrderConfirmed.class,
            headers = @AsyncOperation.Headers(
                    values = {
                            @AsyncOperation.Headers.Header(name = "X-Correlation-ID", description = "Tracing ID"),
                            @AsyncOperation.Headers.Header(name = "eventType" ),
                            @AsyncOperation.Headers.Header(name = "source-system")
                    }
            )
    ))
    @KafkaListener(
            containerFactory = "notificationKafkaListenerContainerFactory",
            topics = "t-order-events",
            groupId = "notification-service-group"
    )
    public void listener(ConsumerRecord<String, String> requestRecord, @Header("eventType") String eventType, Acknowledgment ack) {
        log.info("Event Received In Notification.EventType:{}, Key: {}, Partition: {}", eventType, requestRecord.key(), requestRecord.partition());

        try {
            // 1. Process Business Logic (e.g., Sending Notification)
            // Recommendation: Map to a specific DTO for cleaner code
//            log.info("Headers Value:{} ", eventType);
            //-----------eventType = orderConfirm, orderStatusUpdate,
            mailEventProcessor.processRawEvent(requestRecord.value(), eventType);
//            log.info("Event Processed : {}",requestRecord);
//            log.info("MailRequestRec: {}", requestMap);
//            processNotification(record.value());
            // 2. Commit manually only AFTER success
            // This ensures if the app crashes before this line, Kafka will redeliver
            ack.acknowledge();
        } catch (Exception  e) {
            log.error("Failed to process order. Not acknowledging so message stays in Kafka.");
//            log.error("Failed Record: {}", requestRecord.value());
            log.debug("Failed Casting Message",e);
            // DO NOT acknowledge here.
            // Depending on your ErrorHandler, the message will either retry or move to DLT.
            // This triggers the DefaultErrorHandler and moves the message to the DLT.
            throw new RuntimeException("Deserilization failed, routing to DLT", e);
        }
    }

}//EC
