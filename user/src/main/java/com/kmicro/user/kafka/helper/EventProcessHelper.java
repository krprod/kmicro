package com.kmicro.user.kafka.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.component.OutboxHelper;
import com.kmicro.user.constants.KafkaConstants;
import com.kmicro.user.constants.Status;
import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.OutboxEntity;
import com.kmicro.user.exception.UserNotFoundException;
import com.kmicro.user.kafka.schemas.SharedUserDetails;
import com.kmicro.user.service.UserService;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessHelper {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final MapCreationHelper mapCreationHelper;
    private final OutboxHelper outboxHelper;

    public String getUserDetailMap(Long userID, Long addressID, String requestID) {
        // -- ASSUMING THAT USER EXIST/CREATED BEFORE ORDER CREATION, NO ANONYMOUS CHECKOUT ALLOWED

        try {
            Map<String, Object> userDetailMap = new HashMap<>();
            Map<String, Object> addressMap = new HashMap<>();
            Map<String, Object> mailMap = new HashMap<>();
           UserDTO userDTO  = userService.getUserById(userID, true);
           AddressDTO address =  userDTO.getAddresses().stream().filter((dto)-> dto.getId() == addressID).toList().getFirst();

           mailMap.put("notification_id", requestID);
            //--- Add User details
            mapCreationHelper.addUserMap(userDTO, userDetailMap);
            //--- Adding Address Or AddressID: 0
            mapCreationHelper.addAddressMap(address, addressMap);

            mailMap.put(KafkaConstants.DT_ADDRESS_NODE,addressMap);
            mailMap.put(KafkaConstants.DT_USER_DATA,userDetailMap);

            return objectMapper.writeValueAsString(mailMap);
       }catch (UserNotFoundException e){
           log.info("UserNotFoundException For UserID:{}, Notification: {}",userID, requestID);
       }catch (NoSuchElementException e){
            log.info("No Address Found For AddressID:{}, UserID: {}", addressID,userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = KafkaConstants.USERS_TOPIC,
            description = "Publishes a message to verify new registered user email",
            payloadType = SharedUserDetails.class,
            headers = @AsyncOperation.Headers(
                    schemaName = KafkaConstants.SYSTEM_USER+"-outgoing-headers",
                    description = "User Service outgoing Request Headers",
                    values = {
                            @AsyncOperation.Headers.Header(name = "event-type" , value =  KafkaConstants.ET_SHARE_USER_DETAILS),
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
                            example = KafkaConstants.USER_KEY_PREFIX + "916e1396-2579-47d1-9518-421478f205fb"
                    )
            )
    )
    public void shareUserDetailsToNotification(Long userID, Long addressID, String requestID){
        String userDetails =  this.getUserDetailMap(userID,addressID,requestID);

        if(!StringUtils.hasText(userDetails)) throw new RuntimeException("NO Detail Map Found");

        OutboxEntity outboxEntity =  OutboxEntity.builder()
                .topic( KafkaConstants.USERS_TOPIC)
                .aggregateId(requestID)
                .eventType(KafkaConstants.ET_SHARE_USER_DETAILS)
                .targetSystem(KafkaConstants.SYSTEM_NOTIFICATION)
                .payload(userDetails)
                .status(Status.PENDING.name())
                .createdAt(Instant.now())
                .build();

        outboxHelper.saveEventInOutbox(outboxEntity);
    }

/*    @Transactional
    public OutboxEntity saveEventInOutbox(OutboxEntity entity){
          return  outboxRepository.save(entity);
    }

    public OutboxEntity makeOutboxEntity(Map<String, Object> userDetails, Long userID, String status, String requestID){
       try {
           return OutboxEntity.builder()
                   .topic(AppContants.USERS_TOPIC)
                   .aggregateId(requestID)
                   .eventType(AppContants.ET_SHARE_USER_DETAILS)
                   .targetSystem(AppContants.SYSTEM_NOTIFICATION)
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
    }*/




}//EC
