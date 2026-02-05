package com.kmicro.user.kafka.producers;

import com.kmicro.user.component.OutboxHelper;
import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.constants.Status;
import com.kmicro.user.entities.OutboxEntity;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.kafka.helper.MapCreationHelper;
import com.kmicro.user.kafka.schemas.VerificationAndReverifyUserEmail;
import com.kmicro.user.kafka.schemas.WelcomeUserMail;
import com.kmicro.user.mapper.UserMapper;
import com.kmicro.user.service.VerificationService;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class ExternalEventProducers {
//    private final ObjectMapper objectMapper;
    private final OutboxHelper outboxHelper;
    private final VerificationService verificationService;
//    private final EventProcessHelper eventProcessHelper;
    private final MapCreationHelper mapCreationHelper;

    ExternalEventProducers(OutboxHelper outboxHelper,
                           VerificationService verificationService, MapCreationHelper mapCreationHelper){
//        this.objectMapper = objectMapper;
        this.outboxHelper = outboxHelper;
        this.verificationService = verificationService;
         this.mapCreationHelper = mapCreationHelper;
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

         String mailbody = mapCreationHelper.createEmailVerifcationMap(UserMapper.EntityToDTO(userE),"",verificationLink);

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

        String mailbody = mapCreationHelper.createWelcomEmailMap(UserMapper.EntityToDTO(userE), "",couponCode);

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

    public void passwordResetOrForgetNotification(UserEntity userEntity, String link, String aggregatedKey){

        String mailbody = mapCreationHelper.createPasswordResetEmailMap(UserMapper.EntityToDTO(userEntity),"","");

        OutboxEntity outboxEntity = OutboxEntity.builder()
                .topic( KafkaConstants.USERS_TOPIC)
                .aggregateId(aggregatedKey)
                .eventType(KafkaConstants.ET_PASSWORD_RESET)
                .targetSystem( KafkaConstants.SYSTEM_NOTIFICATION)
                .payload(mailbody)
                .status(Status.PENDING.name())
                .createdAt(Instant.now())
                .build();

        outboxHelper.saveEventInOutbox(outboxEntity);
    }



}//EC