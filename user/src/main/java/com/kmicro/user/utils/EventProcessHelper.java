package com.kmicro.user.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.exception.UserNotFoundException;
import com.kmicro.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessHelper {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public Map<String, Object> getUserDetailMap(Long userID, Long addressID, String requestID) {
        // -- ASSUMING THAT USER EXIST/CREATED BEFORE ORDER CREATION, NO ANONYMOUS CHECKOUT ALLOWED
        Map<String, Object> userDetails = new HashMap<>();
        try {
           UserDTO userDTO  = userService.getUserById(userID, true);
           AddressDTO address =  userDTO.getAddresses().stream().filter((dto)-> dto.getId() == addressID).toList().getFirst();

            userDetails.put("name",userDTO.getFirstName());
            userDetails.put("contact",userDTO.getContactNumber());
            userDetails.put("email",userDTO.getEmail());
            userDetails.put("notification_id", requestID);
            userDetails.put("user_id", userDTO.getId());

            if(address != null){
                userDetails.put("address_id",address.getId());
                userDetails.put("country",address.getCountry());
                userDetails.put("state", address.getState());
                userDetails.put("city",address.getCity());
                userDetails.put("shipping_address",address.getAddressLine1()+" "+ address.getAddressLine2());
                userDetails.put("zip_code",address.getZipCode());
            }else {
                userDetails.put("address_id",0);
            }

       }catch (UserNotFoundException e){
           log.info("UserNotFoundException For UserID:{}, Notification: {}",userID, requestID);
       }catch (NoSuchElementException e){
            log.info("No Address Found For AddressID:{}, UserID: {}", addressID,userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userDetails;
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

    public JsonNode getMapObjectFromString(String data){
        try {
            return objectMapper.readTree(data);
        }catch (JacksonException e) {
            log.error("Casting String Msg to JsonNode Failed", e);
            throw new RuntimeException(e);
        }
    }



}//EC
