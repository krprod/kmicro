package com.kmicro.notification.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_address_seq")
    @SequenceGenerator(name = "user_address_seq", allocationSize = 50)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "zip_code")
    private String zipCode;

    private String city;
    private String state;
    private String country;
    private String shipping_address;
}
