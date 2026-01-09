package com.kmicro.notification.repository;

import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationsEntity, UUID> {
    List<NotificationsEntity> findByStatus(Status status);
}
