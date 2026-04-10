package com.kmicro.order.components;

import com.kmicro.order.constants.KafkaConstants;
import com.kmicro.order.entities.OutboxEntity;
import com.kmicro.order.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @SchedulerLock(
            name = "OutboxProcessorTaskLock",
            lockAtMostFor = "15s",
            lockAtLeastFor = "5s"
    )
    @Scheduled(fixedDelay = 30000) // Runs every 10 seconds
    @Transactional
    public void processOutbox() {
        List<OutboxEntity> events = outboxRepository.findByStatus("PENDING");

        for (OutboxEntity event : events) {
            try {
                // 1. Create the base record
                ProducerRecord<String, String> record = new ProducerRecord<>(event.getTopic(), event.getAggregateId(), event.getPayload());

                // 2. Add custom headers (Note: values must be byte[])
                record.headers().add(new RecordHeader("event-type", event.getEventType().getBytes(StandardCharsets.UTF_8)));

                if(StringUtils.contains(event.getAggregateId(), "PMT_")){
                    record.headers().add(new RecordHeader("source-system", KafkaConstants.SYSTEM_PAYMENT.getBytes(StandardCharsets.UTF_8)));
                }else {
                    record.headers().add(new RecordHeader("source-system", KafkaConstants.SYSTEM_ORDER.getBytes(StandardCharsets.UTF_8)));
                }

                record.headers().add(new RecordHeader("target-system", event.getTargetSystem().getBytes(StandardCharsets.UTF_8)));
                // Send to Kafka
                kafkaTemplate.send(record)
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
