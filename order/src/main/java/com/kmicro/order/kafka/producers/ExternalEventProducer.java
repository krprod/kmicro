package com.kmicro.order.kafka.producers;

import com.kmicro.order.constants.KafkaConstants;
import com.kmicro.order.constants.Status;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.entities.OutboxEntity;
import com.kmicro.order.entities.PaymentEntity;
import com.kmicro.order.kafka.helpers.EventDataCreator;
import com.kmicro.order.kafka.schemas.OrdersListenerParentSchema;
import com.kmicro.order.kafka.schemas.PaymentStatusUpdate;
import com.kmicro.order.repository.OutboxRepository;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalEventProducer {
    private final OutboxRepository outboxRepository;
    private final EventDataCreator eventDataCreator;

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = KafkaConstants.ORDER_TOPIC,
            description = "Publishes a message to send email notification",
            payloadType = OrdersListenerParentSchema.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_ORDER+"-outgoing-headers",
                    description = "Order-Cart Service outgoing Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_ORDER_CONFIRMED
                                    +" | "+KafkaConstants.ET_ORDER_STATUS_UPDATED +" | "+KafkaConstants.ET_ORDER_CREATED ),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_ORDER),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_NOTIFICATION)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.ORDER_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the orders (order_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.ORDER_KEY_PREFIX+"mailReq_3"
                    )
            )
    )
    public OutboxEntity orderNTF(OrderDTO order, String eventType, boolean newOrder){

        String payload = newOrder ? eventDataCreator.orderConfirmMail(order) : eventDataCreator.orderStatusChangeMail(order);
        log.info("Sending Order Status Related Notification");
        return outboxRepository.save(
                OutboxEntity.builder()
                        .topic(KafkaConstants.ORDER_TOPIC)
                        .aggregateId("mailReq_"+order.getId().toString())
                        .eventType(eventType)
                        .targetSystem(KafkaConstants.SYSTEM_NOTIFICATION)
                        .payload(payload)
                        .status(Status.PENDING.name())
                        .createdAt(Instant.now())
                        .build()
        );
    }


    @AsyncPublisher(operation = @AsyncOperation(
            channelName = KafkaConstants.PAYMENT_TOPIC,
            description = "Publishes a message to share payment status with ORDER and NOTIFICATION Service",
            payloadType = PaymentStatusUpdate.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_PAYMENT+"-outgoing-headers",
                    description = "Payment Service outgoing Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_PAYMENT_STATUS_UPDATE),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_PAYMENT),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_NOTIFICATION )
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.ORDER_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the orders (order_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.ORDER_KEY_PREFIX+"PMT_payStatus_3"
                    )
            )
    )
    public OutboxEntity paymentNTF(PaymentEntity payment, OrderDTO orderEntity, String eventType, boolean paymentSuccess){

        String payload = eventDataCreator.paymentStatus(payment, paymentSuccess, orderEntity);

        log.info("Sending Payment Status Update Notification");
        return outboxRepository.save(
                OutboxEntity.builder()
                        .topic(KafkaConstants.PAYMENT_TOPIC)
                        .aggregateId("PMT_payStatus_"+orderEntity.getId().toString())
                        .eventType(eventType)
                        .targetSystem(KafkaConstants.SYSTEM_NOTIFICATION)
                        .payload(payload)
                        .status(Status.PENDING.name())
                        .createdAt(Instant.now())
                        .build()
        );
    }


}//EC
