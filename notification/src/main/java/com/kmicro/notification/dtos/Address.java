package com.kmicro.notification.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String name;
    private String street1;
    private String street2;
    private String city;
    private String postalCode;
    private String state;
    private String phone;
    private String email;
}
