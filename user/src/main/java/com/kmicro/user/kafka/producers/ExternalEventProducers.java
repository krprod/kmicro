package com.kmicro.user.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.component.OutboxHelper;
import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.constants.Status;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.OutboxEntity;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.kafka.schemas.VerificationAndReverifyUserEmail;
import com.kmicro.user.kafka.schemas.WelcomeUserMail;
import com.kmicro.user.mapper.UserMapper;
import com.kmicro.user.service.VerificationService;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
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

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = KafkaConstants.USERS_TOPIC,
            description = "Publishes a message to verify new registered user email",
            payloadType = VerificationAndReverifyUserEmail.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_USER+"-outgoing-headers",
                    description = "User Service outgoing Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_VERIFY_EMAIL),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_USER),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_NOTIFICATION)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.NOTIFICATION_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the user (user_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.USER_KEY_PREFIX+"verifyNewCustomer_1 | "+KafkaConstants.USER_KEY_PREFIX+"verifyReattempt_1"
                    )
            )
    )
    public void emailVerificationNotification(UserEntity userE, String link, String aggregatedKey){

        String verificationLink  = null != link && !link.isEmpty() ? link : verificationService.generateNewToken(userE.getId());
        String aggKey = null != aggregatedKey && !aggregatedKey.isEmpty()? aggregatedKey+userE.getId() : "verifyNewCustomer_"+userE.getId();

         String mailbody = this.createEmailVerifcationMap(UserMapper.EntityToDTO(userE),"",verificationLink);

        OutboxEntity outboxEntity = OutboxEntity.builder()
                                                                                                        .topic( KafkaConstants.USERS_TOPIC)
                                                                                                        .aggregateId(aggKey)
                                                                                                        .eventType(KafkaConstants.ET_VERIFY_EMAIL)
                                                                                                        .targetSystem( KafkaConstants.SYSTEM_NOTIFICATION)
                                                                                                        .payload(mailbody)
                                                                                                        .status(Status.PENDING.name())
                                                                                                        .createdAt(Instant.now())
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

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = KafkaConstants.USERS_TOPIC,
            description = "Publishes a welcome message when new user email verified successfully",
            payloadType = WelcomeUserMail.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_USER+"-outgoing-headers",
                    description = "User Service outgoing Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_WELCOME_USER),
                            @AsyncOperation.Headers.Header(name = "source-system", value = KafkaConstants.SYSTEM_USER),
                            @AsyncOperation.Headers.Header(name = "target-system", value = KafkaConstants.SYSTEM_NOTIFICATION)
                    }
            )
    ))
    @KafkaAsyncOperationBinding(
            groupId = KafkaConstants.NOTIFICATION_GROUP_ID,
            messageBinding =   @KafkaAsyncOperationBinding.KafkaAsyncMessageBinding(
                    key = @KafkaAsyncOperationBinding.KafkaAsyncKey(
                            description = "The unique identifier for the user (user_id)",
                            type = KafkaAsyncOperationBinding.KafkaAsyncKey.KafkaKeyTypes.STRING_KEY,
                            example = KafkaConstants.USER_KEY_PREFIX+"welcomeNewUser_1"
                    )
            )
    )
    public void welcomEmailNotification(UserEntity userE, String couponCode, String aggregatedKey){

        String aggKey = null != aggregatedKey && !aggregatedKey.isEmpty()? aggregatedKey+userE.getId() : "welcome_"+userE.getId();

        String mailbody = this.createWelcomEmailMap(UserMapper.EntityToDTO(userE), "",couponCode);

        OutboxEntity outboxEntity = OutboxEntity.builder()
                .topic( KafkaConstants.USERS_TOPIC)
                .aggregateId(aggKey)
                .eventType(KafkaConstants.ET_WELCOME_USER)
                .targetSystem( KafkaConstants.SYSTEM_NOTIFICATION)
                .payload(mailbody)
                .status(Status.PENDING.name())
                .createdAt(Instant.now())
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