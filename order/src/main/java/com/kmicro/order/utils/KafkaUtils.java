package com.kmicro.order.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaUtils {

    @Lazy // Connection only happens when this is first called
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderEvent(String orderId, String jsonPayload){
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("t-order-placed", orderId, jsonPayload);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Order {} published successfully at offset {}",
                        orderId, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to publish order {}. This requires immediate attention!", orderId, ex);
                // Industry Practice: Write to a local 'outbox' table to retry later
            }
        });
    }


}//EC
