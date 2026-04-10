package com.kmicro.user.dtos;

public record UserRegistrationRecord(
        String email,
        String login_name,
      String password) {
}
