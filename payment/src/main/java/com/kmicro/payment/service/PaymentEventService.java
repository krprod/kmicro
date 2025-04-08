package com.kmicro.payment.service;

//import com.kmicro.avro.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.payment.dtos.OrderResponse;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class PaymentEventService {

    private static final String TOPIC = "payment-events";

    @Autowired
    private Producer<String, String> kafkaProducer;

    @Autowired
    ObjectMapper objectMapper;

    public void processPaymentKafka(OrderResponse order) {
        // Simulate payment processing
            try {
                HashMap<String, String> event = new HashMap<>();
                event.put("order_id", order.getOrderID().toString());
                event.put("payment_status", order.getPaymentStatus());
                event.put("trasaction_id", order.getTransactionID());

                String eventString = objectMapper.writeValueAsString(event);

                kafkaProducer.send(new ProducerRecord<String, String>(TOPIC, null, event.get("order_id").toString(), eventString) ); // Key = orderId for partitioning

                System.out.println("Published payment event for order: " + event.get("order_id"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

}//endClass
