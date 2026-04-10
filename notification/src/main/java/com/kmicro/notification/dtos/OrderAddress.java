package com.kmicro.notification.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddress {
    @JsonProperty("address_id") @Min(0) int addressID;
    @JsonProperty("shipping_address")String street;
    String city;
    @JsonProperty("zip_code") String zipCode;
    String country;
}
