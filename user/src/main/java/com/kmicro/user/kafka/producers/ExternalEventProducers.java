package com.kmicro.user.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.component.OutboxHelper;
import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.constants.Status;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.OutboxEntity;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.mapper.UserMapper;
import com.kmicro.user.service.VerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ExternalEventProducers {
    private final ObjectMapper objectMapper;
    private final OutboxHelper outboxHelper;
    private final VerificationService verificationService;

    ExternalEventProducers(ObjectMapper objectMapper, OutboxHelper outboxHelper, VerificationService verificationService){
        this.objectMapper = objectMapper;
        this.outboxHelper = outboxHelper;
        this.verificationService = verificationService;
    }

    public void emailVerificationNotification(UserEntity userE, String link, String aggregatedKey){

        String verificationLink  = null != link && !link.isEmpty() ? link : verificationService.generateNewToken(userE.getId());
        String aggKey = null != aggregatedKey && !aggregatedKey.isEmpty()? aggregatedKey+userE.getId() : "newCustomer_"+userE.getId();

         String mailbody = this.createEmailVerifcationMap(UserMapper.EntityToDTO(userE),"",verificationLink);

        OutboxEntity outboxEntity = OutboxEntity.builder()
                                                                                                        .topic( KafkaConstants.USERS_TOPIC)
                                                                                                        .aggregateId(aggKey)
                                                                                                        .eventType(KafkaConstants.ET_VERIFY_EMAIL)
                                                                                                        .targetSystem( KafkaConstants.SYSTEM_NOTIFICATION)
                                                                                                        .payload(mailbody)
                                                                                                        .status(Status.PENDING.name())
                                                                                                        .createdAt(LocalDateTime.now())
                                                                                                        .build();

        outboxHelper.saveEventInOutbox(outboxEntity);
    }

    public String createEmailVerifcationMap(UserDTO user, String msgLine, String verifyLink){
        try {
            Map<String, Object> userMap = new HashMap<>();
            Map<String, Object> mailMap = new HashMap<>();
            userMap.put("title", "Verify Your Email");
            userMap.put("greetingByName", "Hi "+user.getLogin_name());
            userMap.put("msgLine1", "Click on the below link button to verify your email address");
            userMap.put("verifyLink", verifyLink);

            mailMap.put("sendto",user.getEmail());
            mailMap.put("subject", "Email verification");
            mailMap.put("body",userMap);
            return objectMapper.writeValueAsString(mailMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String createWelcomEmailMap(UserDTO user, String msgLine, String couponCode){
        ;
        try {
            String coupon  = null != couponCode && !couponCode.isEmpty() ? couponCode : "WELCOME100";
            Map<String, Object> userMap = new HashMap<>();
            Map<String, Object> mailMap = new HashMap<>();
            userMap.put("title", "Welcome To Kmicro");
            userMap.put("userName", "Hi "+ StringUtils.capitalize(user.getFirstName())+" "+ StringUtils.capitalize(user.getLastName()));
            userMap.put("msgLine1", "We're thrilled to have you here. To get you started, use the code below for 10% off your first order.");
            userMap.put("discountCode", coupon);

            mailMap.put("sendto",user.getEmail());
            mailMap.put("subject", "Welcome To Kmicro");
            mailMap.put("body",userMap);
            return objectMapper.writeValueAsString(mailMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void welcomEmailNotification(UserEntity userE, String couponCode, String aggregatedKey){

        String aggKey = null != aggregatedKey && !aggregatedKey.isEmpty()? aggregatedKey+userE.getId() : "welcome"+userE.getId();

        String mailbody = this.createWelcomEmailMap(UserMapper.EntityToDTO(userE), "",couponCode);

        OutboxEntity outboxEntity = OutboxEntity.builder()
                .topic( KafkaConstants.USERS_TOPIC)
                .aggregateId(aggKey)
                .eventType(KafkaConstants.ET_WELCOME_USER)
                .targetSystem( KafkaConstants.SYSTEM_NOTIFICATION)
                .payload(mailbody)
                .status(Status.PENDING.name())
                .createdAt(LocalDateTime.now())
                .build();

        outboxHelper.saveEventInOutbox(outboxEntity);
    }

    public String createPasswordResetEmailMap(UserDTO user, String msgLine, String verifyLink){
        try {
            Map<String, Object> userMap = new HashMap<>();
            Map<String, Object> mailMap = new HashMap<>();
            userMap.put("title", "Verify Your Email");
            userMap.put("greetingByName", "Hi "+user.getLogin_name());
            userMap.put("msgLine1", "Click on the below link button to verify your email address");
            userMap.put("verifyLink", verifyLink);

            mailMap.put("sendto",user.getEmail());
            mailMap.put("subject", "Email verification");
            mailMap.put("body",userMap);
            return objectMapper.writeValueAsString(mailMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}//EC