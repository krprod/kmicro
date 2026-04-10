package com.kmicro.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserDTO {

    private Long id;
    private String login_name;
    //-------- Updatable Fields
    private String email;
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String LastName;

    @JsonProperty("contact")
    private String contactNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String avtar;
    //-------- Updatable Fields

    private boolean isLoggedIn;
    @JsonProperty( "last_login_time")
    private LocalDateTime lastloginTime;

    private Double latitude ;
    private Double longitude;

    @JsonProperty("is_active")
    private  boolean isActive;

    @JsonProperty("is_verified")
    private  boolean isVerified;
//    private lastLogin;
//    createdAt
    private List<AddressDTO> addresses;
    private Set<String> roles;
}
