package com.kmicro.order.kafka.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.constants.KafkaConstants;
import com.kmicro.order.constants.Status;
import com.kmicro.order.dtos.ProcessPaymentRecord;
import com.kmicro.order.entities.PaymentEntity;
import com.kmicro.order.kafka.helpers.PaymentHelper;
import com.kmicro.order.kafka.producers.InternalEventProducer;
import com.kmicro.order.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProcessor {

    private final ObjectMapper objectMapper;
    private final PaymentHelper paymentHelper;
    private final PaymentRepository paymentRepository;
    private final InternalEventProducer internalEventProducer;

    @Transactional
    public <T> void processRawEvent(T requestRecord, String eventType) {
        if(!eventType.equals(KafkaConstants.ET_NEW_PAYMENT_REQ)) return;

        ProcessPaymentRecord paymentRecord = this.getPaymentRecord(requestRecord.toString());
        PaymentEntity payment = paymentHelper.getNewPaymentEntity(paymentRecord);
        try {
            Thread.sleep(5000);
            double  failureProbability = 0.5;
            double chance = ThreadLocalRandom.current().nextDouble();
//            if (chance < failureProbability) {
//                log.error(
//                        "❌ RANDOM ERROR: Chaos Monkey triggered! (Chance: {} < Probability: {})",
//                        String.format("%.2f", chance), failureProbability
//                );
//                throw  new RuntimeException("---- CHAOS CREATED ----");
//            }

            payment.setStatus(Status.PAYMENT_SUCCESS.name());
            log.info("Payment Succeed");

        } catch (Exception e) {

            payment.setStatus(Status.PAYMENT_FAILED.name());
            payment.setReason(e.getMessage());
            log.info("---Payment Failed--", e);

        } finally {
            payment.setCreatedAt(Instant.now());
            payment.setUpdatedAt(Instant.now());
            PaymentEntity savedPayment = paymentRepository.save(payment);
            internalEventProducer.paymentResponse(savedPayment, KafkaConstants.ET_PAYMENT_RES);
        }
    }

    private ProcessPaymentRecord getPaymentRecord(String data){
        try {
            JsonNode paymentJson = objectMapper.readTree(data);
            return new ProcessPaymentRecord(
                    paymentJson.get("orderId").asLong(),
                    paymentJson.get("amount").asDouble(),
                    paymentJson.get("method").asText(),
                    paymentJson.get("userID").asLong(),
                    paymentJson.get("shipping_fee").asDouble()
                    );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}//EC
