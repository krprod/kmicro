package com.kmicro.notification.kafka.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kmicro.notification.constansts.KafkaConstants;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.entities.OutboxEntity;
import com.kmicro.notification.kafka.helper.EventProcessingHelper;
import com.kmicro.notification.kafka.schemas.RequestUserDetail;
import com.kmicro.notification.utils.DBOps;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalEventProducers {
    private final DBOps DBOps;
    private final EventProcessingHelper eventProcessingHelper;
    private final ObjectMapper objectMapper;


    public void requestUserData(Integer userID, NotificationsEntity notification){
//        Optional<UserDataEntity> userData = DBOps.getUserFromDB(userID);
//        Map<String, Object> mailBody = notification.getMailBody();

//        if(userData.isPresent()){
//            eventProcessingHelper.updateUserDataInMailBody(userData.get(), mailBody);
            // update ReciepentName, sendto, mailbody in table
//            outboxUtils.updateMailBodyMapping(userData.get(), entity);
//            entity.setStatus(Status.PENDING);
//            entity.setUpdatedAt(Instant.now());
//            log.info("UserDetail Found In DB, Notification Table Updated Successfully");
//        } else {
            // Get User From Users Serivce Via Kafka Event
//            JsonNode node = objectMapper.convertValue(entity.getMailBody(), JsonNode.class);
//            kafkaTemplate.send(
//                            outboxUtils.generateUserEventRecord(
//                                    objectMapper.writeValueAsString(entity.getRecipientId())
//                                    ,objectMapper.writeValueAsString(     Map.of(
//                                            "user_id",entity.getRecipientId(),
//                                            "address_id", node.get("details").get("address_id").asText(),
//                                            "notification_id",entity.getId()
//                                    ))
//                            ))
//                    .whenComplete((result, ex) -> {
//                        if (ex == null) {
////                                   entity.setStatus(Status.WAITING_ON_USER_SERVICE);
//                            entity.setUpdatedAt(Instant.now());
////                                log.info("Outbox eventID: {} published to Kafka Topic: {} eventKey: {}", event.getId(), event.getTopic(), event.getAggregateId());
//                            log.info("Event SENT TO USER SERVICE for UserID: {}, NotificationID:{}",entity.getRecipientId(), entity.getId());
//
//                            DBOps.saveDataInDB(entity);
//                        } else {
////                                handleFailure(event);
//                            log.info("Event FAILED");
//                        }
//                    });
//        }
    }

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = KafkaConstants.USERS_TOPIC,
            description = "Publishes a message to verify new registered user email",
            payloadType = RequestUserDetail.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_NOTIFICATION+"-outgoing-headers",
                    description = "Notification Service outgoing Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_REQUEST_USER_DETAILS),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_NOTIFICATION),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_USER)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.NOTIFICATION_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the user (user_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.NOTIFICATION_KEY_PREFIX+"2d965cea-c31f-491b-a930-3684aed541ed"
                    )
            )
    )
    public void requestUserData(String id, ObjectNode requestData) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .topic(KafkaConstants.USERS_TOPIC)
                .aggregateId(id)
                .eventType(KafkaConstants.ET_REQUEST_USER_DETAILS)
                .targetSystem(KafkaConstants.SYSTEM_USER)
                .payload(requestData.toString())
                .status(Status.PENDING.name())
                .createdAt(Instant.now())
                .build();

        DBOps.saveOutboxEvent(outboxEntity);
    }
}//EC
