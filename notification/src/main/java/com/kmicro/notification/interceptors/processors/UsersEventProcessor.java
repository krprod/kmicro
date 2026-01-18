package com.kmicro.notification.interceptors.processors;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.entities.UserDataEntity;
import com.kmicro.notification.utils.CommonHelperUtils;
import com.kmicro.notification.utils.EmailUtils;
import com.kmicro.notification.utils.NotificationDBUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersEventProcessor {

    private final EmailUtils emailUtils;
    private final NotificationDBUtils notificationDBUtils;
    private final ObjectMapper objectMapper;
    private final CommonHelperUtils commonHelperUtils;

    private final Map<String, String> eventTypeMap = Map.of(
            "userDetailShared", "userDetailShared"
    );

    public <T> void processRawEvent(T data, String eventType){
        if(null == eventTypeMap.get(eventType)) return; // Only Accept Few Event types
        try {
            Map<String, Object> userDetailMap = commonHelperUtils.getDataMapFromContent(data);
            JsonNode  userDetailJSON  = commonHelperUtils.getJsonNodeFromString(data.toString());
            UUID notificationID = UUID.fromString(userDetailJSON.get("notification_id").asText());

            //--- Save user details in UserDataRepository
            UserDataEntity userDataEntity = commonHelperUtils.createUserDataEntity(userDetailJSON);

            Optional<NotificationsEntity> notificationsEntityOptional = notificationDBUtils.findByNotificationID(notificationID);
            if(notificationsEntityOptional.isPresent()){

                NotificationsEntity notificationsEntity = notificationsEntityOptional.get();
                //--- Update MailBodyMap
                Map<String, Object> mailBodyMap = notificationsEntity.getMailBody();
                mailBodyMap.put("details", userDetailMap);

                String greeting = mailBodyMap.get("greetingByName").toString().replaceFirst("TEMPA-\\d+", userDetailJSON.get("name").asText());
                mailBodyMap.put("greetingByName",greeting);

                notificationsEntity.setMailBody(mailBodyMap);

                //--- Update Notification Table columns like recipientName,
                notificationsEntity.setRecipientId(userDetailJSON.get("user_id").asInt());
                notificationsEntity.setSendTo(userDetailJSON.get("email").asText());
                notificationsEntity.setRecipientName(userDetailJSON.get("name").asText());
                notificationsEntity.setStatus(Status.PENDING);

                notificationsEntity.setUpdatedAt(Instant.now());
                userDataEntity.setCreatedAt(Instant.now());

                this.flushEntities(notificationsEntity, userDataEntity);
            }

        } catch (JsonProcessingException e) {
            log.info("---NOT ABLE TO PARSE AND RETRY FOR DATA---: {}",data);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void flushEntity(NotificationsEntity entity){
        notificationDBUtils.saveDataInDB(entity);
        log.info("Entity Flushed Successful");
    }

    private void flushEntities(NotificationsEntity notification, UserDataEntity userData){
        notificationDBUtils.saveBothInDB(notification, userData);
        log.info("Entities Flushed Successful");
    }

}//EC
