package com.kmicro.notification.utils;

import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDBUtils {
    private final NotificationRepository notificationRepository;

    @Transactional
    public  void saveDataInDB(NotificationsEntity notificationsEntity){
//        if(notificationsEntities instanceof List<NotificationsEntity> entityList){
//            notificationRepository.saveAll(notificationsEntities);
//        }
        notificationRepository.save(notificationsEntity);
        log.info("Saved Data In DB");
    }

    @Transactional
    public void updateDeliveryStatus(UUID notificationID, Status status) {
        NotificationsEntity entity = notificationRepository.findById(notificationID)
                .orElseThrow(()-> new RuntimeException("NO ID FOUND: "+ notificationID));
        entity.setStatus(status);
        log.info("notification  ID: {}  ----  status: {}", notificationID, status);
    }

    @Transactional
    public void updateDeliveryStatus(UUID notificationID, Status status, Exception e) {
        NotificationsEntity entity = notificationRepository.findById(notificationID)
                .orElseThrow(()-> new RuntimeException("NO ID FOUND: "+ notificationID));
        entity.setStatus(status);
        entity.setFailureReason(e.getMessage());
        log.info("notification  ID: {}  ----  status: {}", notificationID, status);
    }

}
