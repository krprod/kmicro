package com.kmicro.notification.kafka.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.notification.constansts.KafkaConstants;
import com.kmicro.notification.kafka.processors.MailEventProcessor;
import com.kmicro.notification.kafka.processors.OrdersEventProcessor;
import com.kmicro.notification.kafka.schemas.OrdersListenerParentSchema;
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
public class OrdersListener {

        private final MailEventProcessor mailEventProcessor;
        private final OrdersEventProcessor ordersEventProcessor;
        private final ObjectMapper objectMapper;

    @AsyncListener(operation = @AsyncOperation(
            channelName =  KafkaConstants.ORDER_TOPIC,
            description = "Consumes Incoming Events: "
                    + KafkaConstants.ET_ORDER_CONFIRMERD +" | "+KafkaConstants.ET_ORDER_CREATED +" | "+KafkaConstants.ET_ORDER_STATUS_UPDATED ,
            payloadType = OrdersListenerParentSchema.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_ORDER+"-incoming-headers",
                    description = "Order-Cart Service Incoming Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_ORDER_CONFIRMERD
                                    +" | "+KafkaConstants.ET_ORDER_CREATED +" | "+KafkaConstants.ET_ORDER_STATUS_UPDATED ),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_ORDER),
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
                            example = KafkaConstants.ORDER_KEY_PREFIX+"52"
                    )
            )
    )
    @KafkaListener(
            containerFactory = "notificationKafkaListenerContainerFactory",
            topics = KafkaConstants.ORDER_TOPIC,
            groupId = KafkaConstants.NOTIFICATION_GROUP_ID
    )
    public void listener(ConsumerRecord<String, String> requestRecord,
                         @Header("event-type") String eventType,
                         @Header("source-system") String sourceSystem,
                         @Header("target-system") String targetSystem,
                         Acknowledgment ack) {
        log.info("Order Event Received. EventType: {}, Source: {}, Target: {}, Key: {}, Partition: {}"
                ,eventType, sourceSystem, targetSystem, requestRecord.key(), requestRecord.partition());
        try {
            // 1. Process Business Logic (e.g., Sending Notification)
            // Recommendation: Map to a specific DTO for cleaner code
//            log.info("Headers Value:{} ", eventType);
            //-----------eventType = orderConfirm, orderStatusUpdate,
            ordersEventProcessor.processRawEvent(requestRecord.value(), eventType);
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
