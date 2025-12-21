package com.kmicro.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserDTO {

    private Long id;
    private String login_name;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String avtar;
    private boolean isLoggedIn;

    private Double latitude ;
    private Double longitude;
//    private lastLogin;
//    createdAt
    private List<AddressDTO> addresses;
    private Set<String> roles;
}
