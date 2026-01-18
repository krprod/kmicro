package com.kmicro.notification.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "notification_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDataEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "recipient_name")
    private String recipientName;

    private String email;

    private String contact;
    private String city;
    private String state;
    private String country;
    private String shipping_address;

    @Column(name = "zip_code")
    private String zipCode;
}
