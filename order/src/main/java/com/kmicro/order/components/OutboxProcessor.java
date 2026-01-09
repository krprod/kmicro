package com.kmicro.order.components;

import com.kmicro.order.entities.OutboxEntity;
import com.kmicro.order.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 30000) // Runs every 10 seconds
    @Transactional
    public void processOutbox() {
        List<OutboxEntity> events = outboxRepository.findByStatus("PENDING");

        for (OutboxEntity event : events) {
            try {
                // Send to Kafka
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                // Update status to PROCESSED on success
                                event.setStatus("PROCESSED");
                                outboxRepository.save(event);
                                log.info("Outbox eventID: {} published to Kafka Topic: {} eventKey: {}", event.getId(), event.getTopic(), event.getAggregateId());
                            } else {
                                handleFailure(event);
                            }
                        });
            } catch (Exception e) {
                handleFailure(event);
            }
        }
    }

    private void handleFailure(OutboxEntity event) {
        event.setRetryCount(event.getRetryCount() + 1);
        if (event.getRetryCount() > 5) {
            event.setStatus("FAILED");
        }
        outboxRepository.save(event);
        log.error("Failed to publish outbox event {}", event.getId());
    }



}//EC
