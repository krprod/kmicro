package com.kmicro.order.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentInterceptor{

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(containerFactory = "orderKafkaListenerContainerFactory", topics = "payment-events", groupId = "order-group")
    public void listen(ConsumerRecord<String, String> requestRecord) {
        try {
            JsonNode paymentJson = objectMapper.readValue(requestRecord.value(), JsonNode.class);
           Integer orderID =  paymentJson.get("orderId").asInt();
            String transID = paymentJson.get("transactionId").asText();
            System.out.println("EventRecieved: "+orderID+"-- "+transID);
        } catch (Exception e) {
            log.error("Error while processing email message {}", e);
        }
    }
}
