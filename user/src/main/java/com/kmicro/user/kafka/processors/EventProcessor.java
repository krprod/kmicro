package com.kmicro.user.kafka.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.component.OutboxHelper;
import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.constants.Status;
import com.kmicro.user.entities.OutboxEntity;
import com.kmicro.user.repository.OutboxRepository;
import com.kmicro.user.utils.EventProcessHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

        private final ObjectMapper objectMapper;
        private final EventProcessHelper eventProcessHelper;
        private final OutboxRepository outboxRepository;
        private final OutboxHelper outboxHelper;
        private final Map<String, String> eventTypes = Map.of(
                "requestUserData", "requestUserData"
        );
        private final Set<String> SetOfEventTypes = Set.of(
                KafkaConstants.ET_REQUEST_USER_DETAILS
        );

        public <T> void processRawEvent(T data, String eventType){
                if(!SetOfEventTypes.contains(eventType)) return; // Only Accept Few Event types
               try {

                       JsonNode requestNode = objectMapper.readTree(data.toString());
                       Long userID = requestNode.get("user_id").asLong();
                       Long addressID =requestNode.get("address_id").asLong();
                       String requestID = requestNode.get("notification_id").asText();

                       Optional<OutboxEntity> outboxEntityOptional = outboxRepository.findByAggregateId(requestID);
                       OutboxEntity outboxEntity = null;
                       if(outboxEntityOptional.isPresent()){
                               // check outbox status
                               // --- if pending/failed : Resend Event
                               outboxEntity = outboxEntityOptional.get();

                               if(outboxEntity.getStatus() == Status.FAILED.name())
                                       outboxEntity.setStatus(Status.PENDING.name());

                               log.info("Duplicate  Event Updated for userID: {} and Notification_ID: {}", userID, requestID);
                       }else{
                               Map<String, Object> userDetailMap =  eventProcessHelper.getUserDetailMap(userID,addressID,requestID);
                               if(!userDetailMap.isEmpty()) {
//                                       outboxEntity = eventProcessHelper.makeOutboxEntity(userDetailMap,userID, Status.PENDING.name(), requestID);
                                       outboxEntity = outboxHelper.makeOutboxEntity(userDetailMap,userID,
                                               KafkaConstants.USERS_TOPIC, KafkaConstants.SYSTEM_NOTIFICATION, KafkaConstants.ET_SHARE_USER_DETAILS,  requestID);
                                       log.info("New  Event Created for userID: {} and Notification_ID: {}", userID, requestID);
                               }
                       }
                       if(outboxEntity != null){
                               outboxHelper.saveEventInOutbox(outboxEntity);
                       }
               } catch (Exception e) {
                       throw new RuntimeException(e);
               }
        }


}//EC
