package com.kmicro.order.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CheckoutDetailsDTO {
    String name;
    String email;
    String contact;
    @JsonProperty("address_id") @Min(0) int addressID;
    @JsonProperty("shipping_address")String street;
    String city;
    String state;
    @JsonProperty("zip_code") String zipCode;
    String country;
}
