package com.kmicro.notification.kafka.listeners;

import com.kmicro.notification.constansts.KafkaConstants;
import com.kmicro.notification.kafka.processors.PaymentEventProcessor;
import com.kmicro.notification.kafka.schemas.PaymentStatusUpdate;
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
public class PaymentsListner {

    private final PaymentEventProcessor paymentEventProcessor;

    @AsyncListener(operation = @AsyncOperation(
            channelName =  KafkaConstants.PAYMENT_TOPIC,
            description = "Consumes Incoming Events: "
                    + KafkaConstants.ET_PAYMENT_STATUS_UPDATE +" | "+KafkaConstants.ET_PAYMENT_STATUS_UPDATE  ,
            payloadType = PaymentStatusUpdate.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_ORDER+"-incoming-headers",
                    description = "Order-Cart Service Incoming Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_PAYMENT_STATUS_UPDATE),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_ORDER),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_NOTIFICATION)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.NOTIFICATION_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the order (order_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.ORDER_KEY_PREFIX+"_PMT_payStatus_52"
                    )
            )
    )
    @KafkaListener(
            containerFactory = "notificationKafkaListenerContainerFactory",
            topics = KafkaConstants.PAYMENT_TOPIC,
            groupId = KafkaConstants.NOTIFICATION_GROUP_ID
    )
    public void listener(ConsumerRecord<String, String> requestRecord,
                         @Header("event-type") String eventType,
                         @Header("source-system") String sourceSystem,
                         @Header("target-system") String targetSystem,
                         Acknowledgment ack) {
        log.info("Payment Event Received. EventType: {}, Source: {}, Target: {}, Key: {}, Partition: {}"
                ,eventType, sourceSystem, targetSystem, requestRecord.key(), requestRecord.partition());
        try {
            // 1. Process Business Logic (e.g., Sending Notification)
            // Recommendation: Map to a specific DTO for cleaner code
//            processNotification(record.value());
            if(targetSystem.equals(KafkaConstants.SYSTEM_NOTIFICATION)){
                paymentEventProcessor.processRawEvent(requestRecord.value(),eventType);
            }
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
