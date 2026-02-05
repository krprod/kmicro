package com.kmicro.notification.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.entities.UserDataEntity;
import com.kmicro.notification.repository.NotificationRepository;
import com.kmicro.notification.utils.DBOps;
import com.kmicro.notification.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProcessor {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EmailUtils emailUtils;
    private final DBOps DBOps;
    private final OutboxUtils outboxUtils;

//    @AsyncPublisher(operation = @AsyncOperation(
//            channelName = "user-placed-topic",
//            description = "Publishes a message when a new customer order is created",
//            servers = {"kafka-server"}
////            payloadType = "{}"
//    ))
//
//    @SchedulerLock(
//            name = "UserProcessorTaskLock",
//            lockAtMostFor = "15s",
//            lockAtLeastFor = "5s"
//    )
//    @Scheduled(fixedDelay = 10000) // Runs every 10 seconds
//    @Transactional
    public void processUser() {
        List<NotificationsEntity> waitingNotificationEvents = notificationRepository.findByStatus(Status.REQUEST_USER_SERVICE);

        for (NotificationsEntity entity : waitingNotificationEvents) {
            try {
                // check If User Exists
                Optional<UserDataEntity> userData = DBOps.getUserFromDB(entity.getRecipientId());

               if(userData.isPresent()){
                   // update ReciepentName, sendto, mailbody in table
                   outboxUtils.updateMailBodyMapping(userData.get(), entity);
                   entity.setStatus(Status.PENDING);
                   entity.setUpdatedAt(Instant.now());
                   log.info("UserDetail Found In DB, Notification Table Updated Successfully");
               } else {
                   // Get User From Users Serivce Via Kafka Event
                   JsonNode node = objectMapper.convertValue(entity.getMailBody(), JsonNode.class);
                   kafkaTemplate.send(
                           outboxUtils.generateUserEventRecord(
                                   objectMapper.writeValueAsString(entity.getRecipientId())
                                   ,objectMapper.writeValueAsString(     Map.of(
                                   "user_id",entity.getRecipientId(),
                                   "address_id", node.get("details").get("address_id").asText(),
                                   "notification_id",entity.getId()
                                    ))
                           ))
                           .whenComplete((result, ex) -> {
                               if (ex == null) {
//                                   entity.setStatus(Status.WAITING_ON_USER_SERVICE);
                                   entity.setUpdatedAt(Instant.now());
//                                log.info("Outbox eventID: {} published to Kafka Topic: {} eventKey: {}", event.getId(), event.getTopic(), event.getAggregateId());
                                   log.info("Event SENT TO USER SERVICE for UserID: {}, NotificationID:{}",entity.getRecipientId(), entity.getId());

                                   DBOps.saveDataInDB(entity);
                               } else {
//                                handleFailure(event);
                                   log.info("Event FAILED");
                               }
                           });
               }

                //--------- Chaos Monkey -------------
//                emailUtils.chaosMonkey(false);
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
        log.error("Failed to find user event {}", event.getId());
    }



}//EC
