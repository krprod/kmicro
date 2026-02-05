package com.kmicro.notification.kafka.listeners;

import com.kmicro.notification.constansts.KafkaConstants;
import com.kmicro.notification.kafka.processors.UsersEventProcessor;
import com.kmicro.notification.kafka.schemas.UserListenerParentSchema;
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
public class UsersListerner {

    private final UsersEventProcessor usersEventProcessor;

    @AsyncListener(operation = @AsyncOperation(
            channelName =  KafkaConstants.USERS_TOPIC,
            description = "Consumes Incoming Events: "
                    + KafkaConstants.ET_SHARE_USER_DETAILS +" | "+KafkaConstants.ET_VERIFY_EMAIL +" | "+KafkaConstants.ET_WELCOME_USER ,
            payloadType = UserListenerParentSchema.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_USER+"-incoming-headers",
                    description = "User Service Incoming Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_SHARE_USER_DETAILS +" | "+KafkaConstants.ET_VERIFY_EMAIL +" | "+KafkaConstants.ET_WELCOME_USER ),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_USER),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_NOTIFICATION)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.NOTIFICATION_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the user (user_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.USER_KEY_PREFIX+"verifyNewCustomer_1 | " + KafkaConstants.USER_KEY_PREFIX+"welcomeNewUser_1 | " +
                                    KafkaConstants.USER_KEY_PREFIX+"916e1396-2579-47d1-9518-421478f205fb | "
                    )
            )
    )
    @KafkaListener(
            containerFactory = "notificationKafkaListenerContainerFactory",
            topics = KafkaConstants.USERS_TOPIC,
            groupId = KafkaConstants.NOTIFICATION_GROUP_ID
    )
    public void listener(ConsumerRecord<String, String> requestRecord,
                         @Header("event-type") String eventType,
                         @Header("source-system") String sourceSystem,
                         @Header("target-system") String targetSystem,
                         Acknowledgment ack) {
        log.info("User Event Received. EventType: {}, Source: {}, Target: {}, Key: {}, Partition: {}"
                ,eventType, sourceSystem, targetSystem, requestRecord.key(), requestRecord.partition());
        try {
            //-----------eventType = userDetailShared
            if(targetSystem.equalsIgnoreCase("notification-service")){
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
