package com.kmicro.notification.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.repository.NotificationRepository;
import com.kmicro.notification.utils.ContextCreatorUtils;
import com.kmicro.notification.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final EmailUtils emailUtils;
    private final ContextCreatorUtils contextCreatorUtils;

    @Scheduled(fixedDelay = 10000) // Runs every 10 seconds
    @Transactional
    public void processOutbox() {
        List<NotificationsEntity> pendingNotificationEvents = notificationRepository.findByStatus(Status.PENDING);

        for (NotificationsEntity entity : pendingNotificationEvents) {
            try {
                //--- Update Entity recipient_id,  retry_count, Status, updated_at
//                String frag = emailUtils.getFragment(event.getTemplateName());
//                Map<String, Object> bodyData = emailUtils.getDataMapFromContent(event.getBodyFromPayload());
                String html = emailUtils.getHtmlBodyFromContent(entity.getMailBody(), entity.getFragment());

                //--------- Chaos Monkey -------------
                double  failureProbability = 0.5;
                double chance = ThreadLocalRandom.current().nextDouble();
                if (chance < failureProbability) {
                    log.error("❌ RANDOM ERROR: Chaos Monkey triggered! (Chance: {} < Probability: {})",
                            String.format("%.2f", chance), failureProbability);

                    throw  new RuntimeException("Meri Marzi");
                }
                //--------- Chaos Monkey -------------
                emailUtils.sendMailAsync(entity.getId(), entity.getSendTo(),entity.getSubject(), html);
                entity.setStatus(Status.SENT);
                entity.setUpdatedAt(Instant.now());
                log.info("Mail Passed to MailSender Successfully");
                // Send to Kafka
                /*kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                // Update status to PROCESSED on success
                                event.setStatus(Sta);
                                notificationRepository.save(event);
                                log.info("Outbox eventID: {} published to Kafka Topic: {} eventKey: {}", event.getId(), event.getTopic(), event.getAggregateId());
                            } else {
                                handleFailure(event);
                            }
                        });*/
            } catch (Exception e) {
                handleFailure(entity, e);
            }
        }
    }

    private void handleFailure(NotificationsEntity event, Exception e) {
        event.setRetryCount(event.getRetryCount() + 1);
        if (event.getRetryCount() > 5) {
            event.setStatus(Status.FAILED);
            event.setFailureReason(e.getMessage());
            event.setUpdatedAt(Instant.now());
        }
        log.error("Failure Cause: ", e);
        notificationRepository.save(event);
        log.error("Failed to publish outbox event {}", event.getId());
    }



}//EC
