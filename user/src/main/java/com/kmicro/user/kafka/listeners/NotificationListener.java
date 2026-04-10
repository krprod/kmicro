package com.kmicro.user.kafka.listeners;

import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.kafka.processors.EventProcessor;
import com.kmicro.user.kafka.schemas.ParentSchema;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
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
public class NotificationListener {

    private final EventProcessor eventProcessor;

    @AsyncListener(operation = @AsyncOperation(
            channelName =  KafkaConstants.USERS_TOPIC,
            description = "Consumes Incoming Events: "+ KafkaConstants.ET_REQUEST_USER_DETAILS,
            payloadType = ParentSchema.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_NOTIFICATION+"-incoming-headers",
                    description = "Notification Service Incoming Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_REQUEST_USER_DETAILS),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_NOTIFICATION),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_USER)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.USERS_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the user (user_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.NOTIFICATION_KEY_PREFIX+"1"
                    )
            )
    )

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
