package com.kmicro.order.kafka.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.constants.KafkaConstants;
import com.kmicro.order.constants.Status;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.entities.PaymentEntity;
import com.kmicro.order.kafka.producers.ExternalEventProducer;
import com.kmicro.order.mapper.OrderMapper;
import com.kmicro.order.repository.PaymentRepository;
import com.kmicro.order.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProcessor {

    private final ObjectMapper objectMapper;
    private final OrderUtils orderUtils;
    private final ExternalEventProducer externalEventProducer;
    private final PaymentRepository paymentRepository;

    @Transactional
    public <T> void processRawEvent(T requestRecord, String eventType) {
        if(!eventType.equals(KafkaConstants.ET_PAYMENT_RES)) return;
        boolean isSuccess = false;
        PaymentEntity payment = this.getPaymentRecord(requestRecord.toString());
        OrderEntity orderEntity = orderUtils.getOrderByIdFromDB(payment.getOrderId());

        orderEntity.setTransactionId(payment.getTransactionId());
        orderEntity.setPaymentStatus(payment.getStatus());
//        orderEntity.setPaymentMethod(payment.getMethod());

        if(payment.getStatus().equals(Status.PAYMENT_SUCCESS.name())){
            orderEntity.setStatus(Status.PAYMENT_SUCCESS);
            isSuccess = true;
            log.info("OrderID: {}, payment processed successfully",orderEntity.getId());
        } else if (payment.getStatus().equals(Status.PAYMENT_FAILED.name())) {
            orderEntity.setStatus(Status.PAYMENT_FAILED);
            isSuccess = false;
            log.info("OrderID: {}, payment processing failed",orderEntity.getId());
        }
//        else {
//            orderEntity.setOrderStatus(OrderStatus.PAYMENT_PENDING);
//        }

        OrderEntity savedOrder = orderUtils.saveOrder(orderEntity);
        OrderDTO order = OrderMapper.mapEntityToDTOWithItems(savedOrder, objectMapper);
        externalEventProducer.paymentNTF(payment,order, KafkaConstants.ET_PAYMENT_STATUS_UPDATE, isSuccess);
//        if(isSuccess) externalEventProducer.orderNTF(order, KafkaConstants.ET_ORDER_CONFIRMED, true);
//        externalEventProducer.paymentNTF(payment,orderEntity, KafkaConstants.ET_PAYMENT_STATUS_UPDATE, false);
    }

    private PaymentEntity getPaymentRecord(String data){
        try {
            JsonNode paymentJson = objectMapper.readTree(data);
            return paymentRepository.findById(paymentJson.get("payment_id").asLong())
                    .orElseThrow(()->new RuntimeException("Payment data Not Found"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}//EC
