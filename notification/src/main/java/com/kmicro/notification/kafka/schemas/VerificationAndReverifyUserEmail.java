package com.kmicro.notification.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example = "{\n" +
                "  \"sendto\": \"user@gmail.com\",\n" +
                "  \"subject\": \"Email verification\",\n" +
                "  \"body\": {\n" +
                "    \"greetingByName\": \"Hi userLoginName\",\n" +
                "    \"verifyLink\": \"http://localhost:8085/api/auth/verify?token=geUixoLvbOMwVx-jAa6qyfgvYecVRxvNGoMrOzonaoY\",\n" +
                "    \"title\": \"Verify Your Email\",\n" +
                "    \"msgLine1\": \"Click on the below link button to verify your email address\"\n" +
                "  },\n" +
                "  \"userData\": {\n" +
                "    \"login_name\": \"userLoginName\",\n" +
                "    \"user_id\": 6,\n" +
                "    \"email\": \"user@gmail.com\"\n" +
                "  }\n" +
                "}"
)
public class VerificationAndReverifyUserEmail {
}
