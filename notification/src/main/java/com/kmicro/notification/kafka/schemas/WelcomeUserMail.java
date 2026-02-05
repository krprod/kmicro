package com.kmicro.notification.kafka.schemas;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example = "{\n" +
                "  \"sendto\": \"user@gmail.com\",\n" +
                "  \"subject\": \"Welcome To Kmicro\",\n" +
                "  \"body\": {\n" +
                "    \"discountCode\": \"WELCOME100\",\n" +
                "    \"title\": \"Welcome To Kmicro\",\n" +
                "    \"userName\": \"Hi userFirstName + userLastName\",\n" +
                "    \"msgLine1\": \"We're thrilled to have you here. To get you started, use the code below for 10% off your first order.\"\n" +
                "  },\n" +
                "  \"userData\": {\n" +
                "    \"login_name\": \"userLoginName\",\n" +
                "    \"user_id\": 6,\n" +
                "    \"contact\": \"59874631288\",\n" +
                "    \"name\": \"userFirstName\",\n" +
                "    \"email\": \"user@gmail.com\"\n" +
                "  }\n" +
                "}"
)
public class WelcomeUserMail {
}
