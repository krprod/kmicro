package com.kmicro.payment.service;

import com.kmicro.avro.PaymentEvent;
import com.kmicro.payment.dtos.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class PaymentEventService {

    private static final String TOPIC = "payment-events";

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void processPaymentKafka(OrderResponse order) {
        // Simulate payment processing
        PaymentEvent event = new PaymentEvent();
        event.setPaymentStatus(order.getPaymentStatus());
        event.setTransactionId(order.getTransactionID());
        event.setOrderId(order.getOrderID().toString());
        kafkaTemplate.send(TOPIC, order.getOrderID().toString(),event); // Key = orderId for partitioning
        System.out.println("Published payment event for order: " + order.getOrderID());
    }
}
