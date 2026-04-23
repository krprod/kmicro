package com.kmicro.notification.controller;

import com.kmicro.notification.annotation.RequiresRole;
import com.kmicro.notification.dtos.NotificiationDTO;
import com.kmicro.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Validated
@Tag(name = "Notification Controller", description = "Operations for  Notification Table")
@AllArgsConstructor
@RequiresRole(value = {"ROLE_ADMIN"})
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/all")
    public List<NotificiationDTO> getAllNotification(){
        return notificationService.getAllNotification();
    }

}//EC
