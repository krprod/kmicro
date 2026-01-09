package com.kmicro.notification.service;

import com.kmicro.notification.constansts.Templates;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.mapper.NotificationsMapper;
import com.kmicro.notification.utils.EmailUtils;
import com.kmicro.notification.utils.NotificationDBUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

        private final EmailUtils emailUtils;
        private final NotificationDBUtils notificationDBUtils;

        private final Map<String, Templates> eventFragMap = Map.of(
                "orderConfirm", Templates.FR_ORDER_CONFIRM,
                "orderStatusUpdate", Templates.FR_SHIPPING_UPDATE,
                "newUser", Templates.FR_WELCOME,
                "passwordChange", Templates.FR_SECURITY_ALERT,
                "otp", Templates.FR_OPT_VERIFICATION
        );

        private List<NotificationsEntity> notificationsList = new ArrayList<>();

        public <T> void processRawEvent(T data, String eventType){
               try {
                       Map<String, Object> requestMap = emailUtils.getDataMapFromContent(data);
                       Map<String, Object> mailBodyMap = emailUtils.getDataMapFromContent(requestMap.get("body"));
                       String frag = emailUtils.getFragment(eventFragMap.get(eventType).name());
                       String mailSubject =  requestMap.get("subject").toString();
                       String sendTo =  requestMap.get("sendto").toString();

                       NotificationsEntity entity = NotificationsEntity.builder()
                               .sendTo(sendTo)
                               .fragment(frag)
                               .subject(mailSubject)
                               .mailBody(mailBodyMap)
                               .build();

                       NotificationsMapper.addFixedFields(entity);

                       this.flushEntity(entity);
               } catch (Exception e) {
                       throw new RuntimeException(e);
               }
//                if(notificationsEntities.size() == 10){
//                        this.flushEntities();
//                        notificationsEntities.add(entity);
//                }else {
//                        notificationsEntities.add(entity);
//                }
        }

        private void flushEntities(){
//                notificationDBUtils.saveDataInDB(this.notificationsList);
//                this.notificationsEntities.clear();
        }

        private void flushEntity(NotificationsEntity entity){
                notificationDBUtils.saveDataInDB(entity);
                log.info("Entity Flushed Successful");
        }
}
