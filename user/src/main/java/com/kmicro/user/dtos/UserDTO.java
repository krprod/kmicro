package com.kmicro.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String avtar;
    private boolean isLoggedIn;

//    private lastLogin;
//    createdAt
    private List<AddressDTO> addresses;
}
