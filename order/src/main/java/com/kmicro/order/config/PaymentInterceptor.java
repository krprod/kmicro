package com.kmicro.order.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.interceptor.OrderServiceListner;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentInterceptor{

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    OrderServiceListner orderServiceListner;

    @KafkaListener(containerFactory = "orderKafkaListenerContainerFactory", topics = "payment-events", groupId = "order-group")
    public void listen(ConsumerRecord<String, String> requestRecord) {
        try {
            JsonNode paymentJson = objectMapper.readValue(requestRecord.value(), JsonNode.class);
            log.info("OrderServiceListner Event Recieved from Payement: {}",paymentJson);
            orderServiceListner.updatePaymentInfoInDB(paymentJson);
//           Integer orderID =  paymentJson.get("orderId").asInt();
//            String transID = paymentJson.get("transactionId").asText();

        } catch (Exception e) {
            log.error("Error while processing email message {}", e);
        }
    }
}
