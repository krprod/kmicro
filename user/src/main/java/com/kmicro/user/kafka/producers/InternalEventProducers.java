package com.kmicro.user.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InternalEventProducers {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    InternalEventProducers(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate){
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Map<String, String> defaultHeader(){
        Map<String, String> defaultHeader = new HashMap<>();
        defaultHeader.put("source-system",KafkaConstants.SYSTEM_USER);
        defaultHeader.put("target-system",KafkaConstants.SYSTEM_USER);
        defaultHeader.put("event-type", KafkaConstants.ET_WELCOME_USER);
        return defaultHeader;
    }

    public ProducerRecord<String, String>  userCreated(UserEntity user){
        String payload = new String( convertDataToString(UserMapper.EntityToDTO(user)) );
        String key = "createUser_"+user.getId();
        Map<String, String> headers = defaultHeader();

        ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConstants.USERS_TOPIC, key, payload);

        addHeaders(record,headers);
        sendEvent(record);
        return record;
    }

    public void verifyNewEmail(UserEntity user){

    }

    public void sendEvent(ProducerRecord<String, String> record){
        kafkaTemplate.send(record)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        // Update status to PROCESSED on success
//                        event.setStatus("PROCESSED");
//                        outboxRepository.save(event);
                        log.info("Event published to Kafka --Topic: {}  --eventKey: {}", result.getProducerRecord().topic(), result.getProducerRecord().key());
//                        log.info("Event published to Kafka");
                    } else {
//                        handleFailure(event);
                    }
                });
    }

    public <T> String convertDataToString(T data){
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHeaders(ProducerRecord<String, String> record, Map<String,String>headers){
        headers.forEach((k,v)->{
            record.headers().add(new RecordHeader(k,v.getBytes(StandardCharsets.UTF_8)));
        });
    }

}//EC
