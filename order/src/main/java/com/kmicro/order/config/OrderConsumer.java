package com.kmicro.order.config;

import com.kmicro.avro.PaymentEvent;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@EnableKafka
@Service
public class OrderConsumer {
    @KafkaListener(topics = "payment-events", groupId = "order-group"  )
    public void listen(ConsumerRecord<String, PaymentEvent> record) {
//        System.out.println("Order Service received: " + event.getOrderId() + " - " + event.getPaymentStatus());
//        System.out.println("Order Service received: " + event);
//        if ("completed".equals(event.getPaymentStatus())) {
//            updateOrderStatus(event.getOrderId(), "paid");
//        }

//        GenericRecord genericRecord = record.value();
//        PaymentEvent event = new PaymentEvent(
//                genericRecord.get("orderId").toString(),
//                genericRecord.get("paymentStatus").toString(),
//                genericRecord.get("transactionId").toString()
//        );
//        System.out.println("Received Payment Event: " + event.value().get("orderId") + " - " + event.value().get("paymentStatus") + " - " + event.value().get("transactionId"));
        PaymentEvent event = record.value();
                System.out.println("Received Payment Event: " + event.getOrderId());
//                        System.out.println("Received Payment Event: " + event.value());
    }

    private void updateOrderStatus(String orderId, String status) {
        // Simulate DB update
        System.out.println("Updated order " + orderId + " to status: " + status);
    }
}
