package com.kmicro.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private static final long serialVersionUID = 1L;
    private Long id;

    @JsonProperty("user_id")
    private  Long userId;

    @JsonProperty("address_line1")
    private String addressLine1;
    @JsonProperty("address_line2")
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    @JsonProperty("zip_code")
    private String zipCode;
}
