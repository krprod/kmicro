package com.kmicro.notification.utils;

import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.entities.OutboxEntity;
import com.kmicro.notification.entities.UserAddressEntity;
import com.kmicro.notification.entities.UserDataEntity;
import com.kmicro.notification.repository.NotificationRepository;
import com.kmicro.notification.repository.OutboxRepository;
import com.kmicro.notification.repository.UserAddressRepository;
import com.kmicro.notification.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DBOps {
    private final NotificationRepository notificationRepository;
    private final UserDataRepository userDataRepository;
    private final UserAddressRepository userAddressRepository;
    private final OutboxRepository outboxRepository;

    @Transactional
    public  NotificationsEntity saveDataInDB(NotificationsEntity notificationsEntity){
//        if(notificationsEntities instanceof List<NotificationsEntity> entityList){
//            notificationRepository.saveAll(notificationsEntities);
//        }
        log.info("Saved Data In DB");
        return notificationRepository.save(notificationsEntity);
    }

    @Transactional
    public void saveDataInDB(NotificationsEntity notificationsEntity, UserDataEntity userDataEntity){
        notificationRepository.save(notificationsEntity);
        userDataRepository.save(userDataEntity);
        log.info("Both Entities are Saved Data In DB");
    }

    @Transactional
    public void saveDataInDB(NotificationsEntity notificationsEntity, UserDataEntity userDataEntity, UserAddressEntity userAddressEntity){
        notificationRepository.save(notificationsEntity);
        userDataRepository.save(userDataEntity);
        userAddressRepository.save(userAddressEntity);
        log.info("All Entities are Saved Data In DB");
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

    @Transactional
    public  void saveUserDataInDB(UserDataEntity user){
        userDataRepository.save(user);
        log.info("UserDataEntity Saved In DB");
    }

    @Transactional(readOnly = true)
    public Optional<UserDataEntity> getUserFromDB(Integer userID){
//        log.info("UserDataEntity Saved In DB");
        return userDataRepository.findByUserId(userID);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationsEntity> findByNotificationID(UUID notificationID) {
        return notificationRepository.findById(notificationID);
    }

    @Transactional(readOnly = true)
    public Optional<UserAddressEntity> getAddressByUserId(Integer addressID, Integer userID){
        return userAddressRepository.findByAddressIdAndUserId(addressID, userID);
    }

    @Transactional
    public void saveOutboxEvent(OutboxEntity outboxEntity){
        outboxRepository.save(outboxEntity);
    }

}//EC
