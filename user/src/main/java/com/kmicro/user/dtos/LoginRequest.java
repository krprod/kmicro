package com.kmicro.user.dtos;

//@Getter
//@Setter
//public class AuthenticationRequest {
//    private String username;
//    private String password;
//
//}
public record LoginRequest(String useremail, String password) {
}