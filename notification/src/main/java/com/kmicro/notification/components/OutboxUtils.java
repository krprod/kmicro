package com.kmicro.notification.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.notification.constansts.KafkaConstants;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.entities.UserDataEntity;
import com.kmicro.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public void updateMailBodyMapping(UserDataEntity userData, NotificationsEntity entity) {
        Map<String, Object> details = new HashMap<>();
        Map<String, Object> mailBody = entity.getMailBody();

//        details.put("name", userData.getRecipientName());
//        details.put("contact", userData.getContact());
//        details.put("email", userData.getEmail());
//        details.put("country", userData.getCountry());
//        details.put("city", userData.getCity());
//        details.put("address_id", userData.getAddressId());
//        details.put("shipping_address", userData.getShipping_address());
//        details.put("zip_code", userData.getZipCode());

        mailBody.put("details", details);

        entity.setSendTo(userData.getEmail());
//       entity.setRecipientName(userData.getRecipientName());
    }

    public ProducerRecord<String, String> generateUserEventRecord(String key, String payload){
        // 1. Create the base record
        ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConstants.USERS_TOPIC, key, payload);

        // 2. Add custom headers (Note: values must be byte[])
        record.headers().add(new RecordHeader("event-type", KafkaConstants.ET_REQUEST_USER_DETAILS.getBytes(StandardCharsets.UTF_8)));
        record.headers().add(new RecordHeader("target-system", KafkaConstants.SYSTEM_USER.getBytes(StandardCharsets.UTF_8)));
        record.headers().add(new RecordHeader("source-system", KafkaConstants.SYSTEM_NOTIFICATION.getBytes(StandardCharsets.UTF_8)));
        return  record;
    }
}//EC
