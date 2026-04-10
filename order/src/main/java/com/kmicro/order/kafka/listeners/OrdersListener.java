package com.kmicro.order.kafka.listeners;

import com.kmicro.order.constants.KafkaConstants;
import com.kmicro.order.kafka.processors.OrderEventProcessor;
import com.kmicro.order.kafka.schemas.PaymentResponseSchema;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncListener;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrdersListener {

    private final OrderEventProcessor orderEventProcessor;

    public OrdersListener(OrderEventProcessor orderEventProcessor){
        this.orderEventProcessor = orderEventProcessor;
    }

    @AsyncListener(operation = @AsyncOperation(
            channelName =  KafkaConstants.ORDER_TOPIC,
            description = "Consumes Incoming Events: "
                    + KafkaConstants.ET_PAYMENT_RES +" | "+KafkaConstants.ET_PAYMENT_RES ,
            payloadType = PaymentResponseSchema.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_PAYMENT+"-incoming-headers",
                    description = "Order-Cart Service Incoming Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_PAYMENT_RES ),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_PAYMENT),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_ORDER)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.ORDER_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the order (order_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.ORDER_KEY_PREFIX+"payReq_1"
                    )
            )
    )
    @KafkaListener(containerFactory = "orderKafkaListenerContainerFactory",
            topics = KafkaConstants.ORDER_TOPIC,
            groupId = KafkaConstants.ORDER_GROUP_ID)
    public void listen(ConsumerRecord<String, String> requestRecord,
                       @Header("event-type") String eventType,
                       @Header("source-system") String sourceSystem,
                       @Header("target-system") String targetSystem,
                       Acknowledgment ack) {
        log.info("Order Event Received. EventType: {}, Source: {}, Target: {}, Key: {}, Partition: {}"
                ,eventType, sourceSystem, targetSystem, requestRecord.key(), requestRecord.partition());
        try {
            if(targetSystem.equals(KafkaConstants.SYSTEM_ORDER)){
                this.orderEventProcessor.processRawEvent(requestRecord.value(), eventType);
            }else {
                log.info("NOT ORDER SERVICE EVENT ---IGNORE Processing ---REVERT Acknowledgment ");
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error while processing email message {}", e);
        }
    }
}
