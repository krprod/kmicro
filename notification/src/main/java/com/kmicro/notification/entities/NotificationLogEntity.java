package com.kmicro.notification.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @Column(name = "notification_id")
    private Long notificationId;

    @Column(columnDefinition = "TEXT", name = "provider_response")
    private  String providerResponse;

    @Column(name = "error_code")
    private  String errorCode;

}
