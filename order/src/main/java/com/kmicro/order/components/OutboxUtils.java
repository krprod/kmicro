package com.kmicro.order.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.entities.OutboxEntity;
import com.kmicro.order.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxUtils {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public <T> OutboxEntity generatePendingEvent(T data, String aggregateKey,String topicName){
        try {
            return OutboxEntity.builder()
                    .topic(topicName)
                    .aggregateId(aggregateKey)
                    // Serializes the actual object instance 'data'
                    .payload(objectMapper.writeValueAsString(data))
                    .status("WAITING")
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (JsonProcessingException e) {
            // Handle serialization error (e.g., log it or throw a custom runtime exception)
            throw new RuntimeException("Failed to serialize outbox payload", e);
        }
    }

    @Transactional
    public List<OutboxEntity> saveAllEvents(List<OutboxEntity> eventList){
        return outboxRepository.saveAll(eventList);
    }

    public <T> OutboxEntity generatePendingEvent(T data, String aggregateKey,String topicName, String eventType, String SourceSystem){
        try {
            return OutboxEntity.builder()
                    .topic(topicName)
                    .aggregateId(aggregateKey)
                    .eventType(eventType)
                    .sourceSystem(SourceSystem)
                    // Serializes the actual object instance 'data'
                    .payload(objectMapper.writeValueAsString(data))
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (JsonProcessingException e) {
            // Handle serialization error (e.g., log it or throw a custom runtime exception)
            throw new RuntimeException("Failed to serialize outbox payload", e);
        }
    }

}//EC
