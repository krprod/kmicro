package com.kmicro.user.kafka.schemas;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example = "{\n" +
                "  \"sendto\": \"user@mail.com\",\n" +
                "  \"subject\": \"Welcome To Kmicro\",\n" +
                "  \"body\": {\n" +
                "    \"discountCode\": \"WELCOME100\",\n" +
                "    \"title\": \"Welcome To Kmicro\",\n" +
                "    \"userName\": \"Hi userFirstName\",\n" +
                "    \"msgLine1\": \"We're thrilled to have you here. To get you started, use the code below for 10% off your first order.\"\n" +
                "  }\n" +
                "}"
)
public class WelcomeUserMail {
}
