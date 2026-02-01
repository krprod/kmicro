package com.kmicro.user.kafka.listeners;

import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.kafka.processors.EventProcessor;
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
public class NotificationListener {

    private final EventProcessor eventProcessor;

    @KafkaListener(
            containerFactory = "userKafkaListenerContainerFactory",
            topics = KafkaConstants.USERS_TOPIC,
            groupId = KafkaConstants.USERS_GROUP_ID
    )
    public void listener(ConsumerRecord<String, String> requestRecord,
                         @Header("event-type") String eventType,
                         @Header("source-system") String sourceSystem,
                         @Header("target-system") String targetSystem,
                         Acknowledgment ack) {
        log.info(
                "Notification Event Received. EventType: {}, Source: {}, Target: {}, Key: {}, Partition: {}"
                ,eventType,  sourceSystem,targetSystem, requestRecord.key(), requestRecord.partition()
        );
        try {

            //-------eventType =  requestUserData
            if(targetSystem.equalsIgnoreCase(KafkaConstants.SYSTEM_USER)){
                eventProcessor.processRawEvent(requestRecord.value(), eventType);
                log.info("Processing Value: {}", requestRecord.value());
            }else {
                log.info("NOT USER SERVICE EVENT ---IGNORE Processing ---REVERT Acknowledgment ");
            }

            // This ensures if the app crashes before this line, Kafka will redeliver
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
