package com.kmicro.notification.service;

import com.kmicro.notification.dtos.NotificiationDTO;
import com.kmicro.notification.mapper.NotificationsMapper;
import com.kmicro.notification.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private  final NotificationRepository notificationRepository;

    public List<NotificiationDTO> getAllNotification() {
        return   notificationRepository.findAll()
                .stream().map(NotificationsMapper::mapNotificationEntityToDTO).toList();
    }
}
