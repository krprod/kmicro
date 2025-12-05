package com.kmicro.user.dtos;

//@RequiredArgsConstructor
//public class AuthenticationResponse {
//    final String jwt;
//}

public record LoginResponse(String status, String jwt) {
}