package com.kmicro.notification.kafka.processors;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kmicro.notification.constansts.KafkaConstants;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.constansts.Templates;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.entities.UserAddressEntity;
import com.kmicro.notification.entities.UserDataEntity;
import com.kmicro.notification.kafka.helper.EventProcessingHelper;
import com.kmicro.notification.utils.CommonHelperUtils;
import com.kmicro.notification.utils.DBOps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersEventProcessor {

    private final DBOps DBOps;
    private final EventProcessingHelper eventProcessingHelper;
    private final CommonHelperUtils commonHelperUtils;
    private final ObjectMapper objectMapper;

    private final Set<String>SetOfEventTypes = Set.of(
            KafkaConstants.ET_SHARE_USER_DETAILS,
            KafkaConstants.ET_VERIFY_EMAIL,
            KafkaConstants.ET_WELCOME_USER
    );

    public <T> void processRawEvent(T data, String eventType){
        if(!SetOfEventTypes.contains(eventType)) return; // Only Accept Few Event types

        try {

            switch (eventType){
                case KafkaConstants.ET_VERIFY_EMAIL -> this.userVerificationNtf(data);
                case KafkaConstants.ET_WELCOME_USER -> this.userWelcomeNtf(data);
                case KafkaConstants.ET_SHARE_USER_DETAILS -> this.getCaptureUserDetailForOrder(data);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void getCaptureUserDetailForOrder(T data){
            try {
                JsonNode payloadJson = commonHelperUtils.getJsonNodeFromString(data.toString());
//                Map<String, Object> payload = commonHelperUtils.getDataMapFromContent(data);
                Map<String, Object>   userDetailMap  = commonHelperUtils.getDataMapFromContent(payloadJson.get(KafkaConstants.DT_USER_DATA));
                JsonNode  addressJson  = payloadJson.get(KafkaConstants.DT_ADDRESS_NODE);
                JsonNode  userJson  = payloadJson.get(KafkaConstants.DT_USER_DATA);
                UUID notificationID = UUID.fromString(payloadJson.get("notification_id").asText());

                ObjectNode detailMap = objectMapper.createObjectNode();
                detailMap.set(KafkaConstants.DT_USER_DATA, userJson);
                detailMap.set(KafkaConstants.DT_ADDRESS_NODE, addressJson);

                //--- Save user details in UserDataRepository
                UserDataEntity userDataEntity = this.checkUser(userJson);
                UserAddressEntity userAddressEntity = this.checkUserAddress(addressJson);

                Optional<NotificationsEntity> notificationOpt= DBOps.findByNotificationID(notificationID);
                if(notificationOpt.isPresent()){

                    NotificationsEntity notificationsEntity = notificationOpt.get();
                    //--- Update MailBodyMap
                    Map<String, Object> mailBodyMap = notificationsEntity.getMailBody();
                    mailBodyMap.put("details", commonHelperUtils.getDataMapFromContent(detailMap));

                    String greeting = mailBodyMap.get("greetingByName").toString().replaceFirst("TEMPA-\\d+", userJson.get(KafkaConstants.DT_UNAME).asText());
                    mailBodyMap.put("greetingByName",greeting);

                    notificationsEntity.setMailBody(mailBodyMap);

                    //--- Update Notification Table columns like recipientName,
                    notificationsEntity.setRecipientId(userJson.get(KafkaConstants.DT_USER_ID).asInt());
                    notificationsEntity.setSendTo(userJson.get(KafkaConstants.DT_EMAIL).asText());
                    notificationsEntity.setRecipientName(userJson.get(KafkaConstants.DT_UNAME).asText());
                    notificationsEntity.setStatus(Status.PENDING);
                    notificationsEntity.setUpdatedAt(Instant.now());
//                    userDataEntity.setCreatedAt(Instant.now());

                    this.flushEntities(notificationsEntity, userDataEntity, userAddressEntity);
                }
            } catch (JsonProcessingException e) {
                log.info("---NOT ABLE TO PARSE AND RETRY FOR DATA---: {}",data);
            }
    }

    private <T> void userVerificationNtf(T data){
       try{
           JsonNode payloadJson = commonHelperUtils.getJsonNodeFromString(data.toString());
//           Map<String, Object> payload = commonHelperUtils.getDataMapFromContent(data);
           Map<String, Object> mailBodyMap = commonHelperUtils.getDataMapFromContent(payloadJson.get(KafkaConstants.DT_BODY));

           JsonNode userDataJson = payloadJson.get(KafkaConstants.DT_USER_DATA);

           UserDataEntity userDataEntity = this.checkUser(userDataJson);

           NotificationsEntity notification = eventProcessingHelper.getNewNotificationEntity(
                   payloadJson.get(KafkaConstants .DT_SEND_TO).asText(),
                   payloadJson.get(KafkaConstants.DT_SUBJECT).asText(),
                   Templates.FR_VERIFY_USER_EMAIL.getName(),
                   mailBodyMap,
                   commonHelperUtils.getDataMapFromContent(data));

            notification.setRecipientName(userDataJson.get(KafkaConstants.DT_LOGIN_NAME).asText());
            notification.setRecipientId(userDataJson.get(KafkaConstants.DT_USER_ID).asInt());

           this.flushEntities(notification, userDataEntity);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }

    private <T> void userWelcomeNtf(T data){
        try{
            JsonNode payloadJson = commonHelperUtils.getJsonNodeFromString(data.toString());
//            Map<String, Object> payload = commonHelperUtils.getDataMapFromContent(data);
            Map<String, Object> mailBodyMap = commonHelperUtils.getDataMapFromContent(payloadJson.get(KafkaConstants.DT_BODY));
            JsonNode userDataJson = payloadJson.get(KafkaConstants.DT_USER_DATA);

            UserDataEntity userDataEntity = this.checkUser(userDataJson);
            NotificationsEntity notification = eventProcessingHelper.getNewNotificationEntity(
                    payloadJson.get(KafkaConstants .DT_SEND_TO).asText(),
                    payloadJson.get(KafkaConstants.DT_SUBJECT).asText(),
                    Templates.FR_WELCOME.getName(),
                    mailBodyMap,
                    commonHelperUtils.getDataMapFromContent(data));

            notification.setRecipientName(userDataJson.get(KafkaConstants.DT_UNAME).asText());
            notification.setRecipientId(userDataJson.get(KafkaConstants.DT_USER_ID).asInt());
            this.flushEntities(notification, userDataEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UserDataEntity checkUser( JsonNode userDataJson){
        Optional<UserDataEntity> userOpt = DBOps.getUserFromDB(userDataJson.get(KafkaConstants.DT_USER_ID).asInt());
        if(userOpt.isPresent()){
            // ---- update existing user fields ---NEED TO THINK AND WORK ON THIS ---
            return eventProcessingHelper.updateUserData(userOpt.get(), userDataJson);
        }
            //---- create new user entity
            return eventProcessingHelper.initialUserEntityFromJson(userDataJson);
    }

    private UserAddressEntity checkUserAddress(JsonNode addressJson){
        Optional<UserAddressEntity> addressOpt = DBOps.getAddressByUserId(addressJson.get(KafkaConstants.DT_USER_ID).asInt(), addressJson.get(KafkaConstants.DT_ADDRS_ID).asInt());

        if(addressOpt.isPresent()){
            // ---- update existing user fields ---NEED TO THINK AND WORK ON THIS ---
            return eventProcessingHelper.updateAddressEntity(addressOpt.get(), addressJson);
        }
        //---- create new user entity
        return eventProcessingHelper.updateAddressEntity(null,addressJson);
    }

    private void flushEntity(NotificationsEntity entity){
        DBOps.saveDataInDB(entity);
        log.info("Entity Flushed Successful");
    }

    private void flushEntities(NotificationsEntity notification, UserDataEntity userData){
        DBOps.saveDataInDB(notification, userData);
        log.info("Entities Flushed Successful");
    }

    private void flushEntities(NotificationsEntity notification, UserDataEntity userData, UserAddressEntity userAddressEntity){
        DBOps.saveDataInDB(notification, userData, userAddressEntity);
        log.info("Entities Flushed Successful");
    }

}//EC
