package com.kmicro.user.dtos;

import jakarta.validation.constraints.Email;
import lombok.NonNull;

public record LoginRequest(
        @NonNull @Email String email,
        @NonNull @Email String password) {
}