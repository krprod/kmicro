package com.kmicro.user.dtos;

public record UserDetailUpdateRec(
        String firstname,
        String lastname,
        String contact,
        String email,
        String password,
        String avtar
) {
}
