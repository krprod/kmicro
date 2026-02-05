package com.kmicro.notification.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDataEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_data_seq")
    @SequenceGenerator(name = "user_data_seq", allocationSize = 50)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "login_name")
    private String loginName;

    private String email;
    private String contact;

//--- NEED TO REMOVE
/*    @Column(name = "address_id")
    private Integer addressId;
    private String city;
    private String state;
    private String country;
    private String shipping_address;

    @Column(name = "zip_code")
    private String zipCode;*/
}
