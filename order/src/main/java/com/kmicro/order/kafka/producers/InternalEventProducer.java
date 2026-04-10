package com.kmicro.order.kafka.producers;

import com.kmicro.order.constants.KafkaConstants;
import com.kmicro.order.constants.Status;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.dtos.ProcessPaymentRecord;
import com.kmicro.order.entities.OutboxEntity;
import com.kmicro.order.entities.PaymentEntity;
import com.kmicro.order.kafka.helpers.EventDataCreator;
import com.kmicro.order.kafka.schemas.PaymentResponseSchema;
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
public class InternalEventProducer {

    private final OutboxRepository outboxRepository;
    private final EventDataCreator eventDataCreator;

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = KafkaConstants.PAYMENT_TOPIC,
            description = "Publishes a new payment message for  payment Service",
            payloadType = ProcessPaymentRecord.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_ORDER+"-outgoing-headers",
                    description = "Order-Cart Service outgoing Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_NEW_PAYMENT_REQ),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_ORDER),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_PAYMENT)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.PAYMENT_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the orders (order_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.ORDER_KEY_PREFIX+"payReq_3"
                    )
            )
    )
    public OutboxEntity requestPayment(OrderDTO order, String eventType){
        log.info("Sending New Payment Request To Payment Service");
        return outboxRepository.save(
                OutboxEntity.builder()
                        .topic(KafkaConstants.PAYMENT_TOPIC)
                        .aggregateId("payReq_"+order.getId().toString())
                        .eventType(eventType)
                        .targetSystem(KafkaConstants.SYSTEM_PAYMENT)
                        .payload(eventDataCreator.requestPaymentET(order))
                        .status(Status.PENDING.name())
                        .createdAt(Instant.now())
                        .build()
        );
    }

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = KafkaConstants.PAYMENT_TOPIC,
            description = "Publishes a new payment message for  payment Service",
            payloadType = PaymentResponseSchema.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_PAYMENT+"-outgoing-headers",
                    description = "Payment Service outgoing Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_PAYMENT_RES),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_PAYMENT),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_ORDER)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.ORDER_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the orders (order_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.ORDER_KEY_PREFIX+"PMT_payRes_3"
                    )
            )
    )
    public OutboxEntity paymentResponse(PaymentEntity payment, String eventType){
        log.info("Sending Payment Response To Order Service --");
        return outboxRepository.save(
                OutboxEntity.builder()
                        .topic(KafkaConstants.ORDER_TOPIC)
                        .aggregateId("PMT_payRes_"+payment.getOrderId().toString())
                        .eventType(eventType)
                        .targetSystem(KafkaConstants.SYSTEM_ORDER)
                        .payload(eventDataCreator.responsePaymentET(payment))
                        .status(Status.PENDING.name())
                        .createdAt(Instant.now())
                        .build()
        );
    }
}
