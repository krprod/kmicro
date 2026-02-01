package com.kmicro.user.dtos;

import org.hibernate.validator.constraints.Length;

public record UserDetailUpdateRec(
       @Length(min = 3, max = 30, message = "Minimum 3 and Max 30 character") String firstname,
       @Length(min = 3, max = 30, message = "Minimum 3 and Max 30 character") String lastname,
        String contact,
        String email,
        String password,
        String avtar
) {
}
