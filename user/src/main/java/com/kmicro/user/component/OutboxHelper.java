package com.kmicro.user.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.constants.Status;
import com.kmicro.user.entities.OutboxEntity;
import com.kmicro.user.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxHelper {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public OutboxEntity saveEventInOutbox(OutboxEntity entity){
        return  outboxRepository.save(entity);
    }

    public OutboxEntity makeOutboxEntity(Map<String, Object> userDetails, Long userID, String status, String requestID){
        try {
            return OutboxEntity.builder()
                    .topic(KafkaConstants.USERS_TOPIC)
                    .aggregateId(requestID)
                    .eventType(KafkaConstants.ET_SHARE_USER_DETAILS)
                    .targetSystem(KafkaConstants.SYSTEM_NOTIFICATION)
                    .payload(objectMapper.writeValueAsString(userDetails))
                    .status(status)
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.info("OutboxEntity CREATION Failed for userID: {}, notification_id: {}", userID, requestID);
            throw new RuntimeException(e);
        }
    }

    public OutboxEntity makeOutboxEntity(Map<String, Object> mailBodyMap, Long userID, String topic,  String targetSystem, String eventType, String aggregateKey ){
        try {
            return OutboxEntity.builder()
                    .topic(topic)
                    .aggregateId(aggregateKey)
                    .eventType(eventType)
                    .targetSystem(targetSystem)
                    .payload(objectMapper.writeValueAsString(mailBodyMap))
                    .status(Status.PENDING.name())
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.info("OutboxEntity CREATION Failed for userID: {}, aggregateID: {}", userID, aggregateKey);
            throw new RuntimeException(e);
        }
    }

    public OutboxEntity makeOutboxEntity(String mailBody, Long userID, String topic,String targetSystem, String eventType,  String aggregateKey ){
        try {
            return OutboxEntity.builder()
                    .topic(topic)
                    .aggregateId(aggregateKey)
                    .eventType(eventType)
                    .targetSystem(targetSystem)
                    .payload(mailBody)
                    .status(Status.PENDING.name())
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.info("OutboxEntity CREATION Failed for userID: {}, aggregateID: {}", userID, aggregateKey);
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public Boolean checkIfAggregateIDAlreadyExist(String requestID){
        return outboxRepository.existsByAggregateId(requestID);
    }
}//EC
