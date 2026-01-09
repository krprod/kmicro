package com.kmicro.notification.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxUtils {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

//    public <T> OutboxEntity generatePendingEvent(T data, String aggregateKey,String topicName){
//        try {
//            return OutboxEntity.builder()
//                    .topic(topicName)
//                    .aggregateId(aggregateKey)
//                    // Serializes the actual object instance 'data'
//                    .payload(objectMapper.writeValueAsString(data))
//                    .status("PENDING")
//                    .createdAt(LocalDateTime.now())
//                    .build();
//        } catch (JsonProcessingException e) {
//            // Handle serialization error (e.g., log it or throw a custom runtime exception)
//            throw new RuntimeException("Failed to serialize outbox payload", e);
//        }
//    }

    @Transactional
    public List<NotificationsEntity> saveAllEvents(List<NotificationsEntity> eventList){
        return notificationRepository.saveAll(eventList);
    }


}//EC
