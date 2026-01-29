package com.kmicro.notification.kafka.interceptors;

import com.kmicro.notification.kafka.processors.UsersEventProcessor;
import com.kmicro.notification.kafka.schemas.SharedUserDetails;
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
public class UsersListerner {

    private final UsersEventProcessor usersEventProcessor;

    @AsyncListener(operation = @AsyncOperation(
            channelName = "t-user-events",
            description = "Consumes inventory updates to sync local cache",
            headers = @AsyncOperation.Headers(
                    values = {
                            @AsyncOperation.Headers.Header(name = "X-Correlation-ID", description = "Tracing ID"),
                            @AsyncOperation.Headers.Header(name = "eventType" ),
                            @AsyncOperation.Headers.Header(name = "source-system")
                    }
            ),
            payloadType = SharedUserDetails.class
    ))
    @KafkaListener(
            containerFactory = "notificationKafkaListenerContainerFactory",
            topics = "t-user-events",
            groupId = "notification-service-group"
    )
    public void listener(ConsumerRecord<String, String> requestRecord,
                         @Header("eventType") String eventType,
                         @Header("source-system") String sourceSystem,
                         Acknowledgment ack) {
        log.info("User Event Received. EventType: {}, Source: {}, Key: {}, Partition: {}",eventType, sourceSystem, requestRecord.key(), requestRecord.partition());
        try {
            //-----------eventType = userDetailShared
            if(sourceSystem.equalsIgnoreCase("notification-service")){
                usersEventProcessor.processRawEvent(requestRecord.value(), eventType);
                log.info("Processing Value: {}", requestRecord.value());
            }else {
                log.info("NOT NOTIFICATION SERVICE EVENT ---IGNORE Processing ---REVERT Acknowledgment ");
            }
            ack.acknowledge();
        } catch (Exception  e) {
            log.error("User Event Processing Failed. Not acknowledging so message stays in Kafka.");
            log.error("Failed Record: {}", requestRecord.value());
            log.debug("Exception Occurred: ",e);
            // DO NOT acknowledge here.
            // Depending on your ErrorHandler, the message will either retry or move to DLT.
            // This triggers the DefaultErrorHandler and moves the message to the DLT.
            throw new RuntimeException("Deserialization failed, routing to DLT", e);
        }
    }

}//EC
