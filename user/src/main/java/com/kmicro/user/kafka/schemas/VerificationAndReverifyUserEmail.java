package com.kmicro.user.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example = "{\n" +
                "  \"sendto\": \"user@mail.com\",\n" +
                "  \"subject\": \"Email verification\",\n" +
                "  \"body\": {\n" +
                "    \"greetingByName\": \"Hi userLoginName\",\n" +
                "    \"verifyLink\": \"http://localhost:8085/api/auth/verify?token=B8hwJ1mGL-jKEvlBeEVtXwbjhdYP_VTcYrrRBwY1iH8\",\n" +
                "    \"title\": \"Verify Your Email\",\n" +
                "    \"msgLine1\": \"Click on the below link button to verify your email address\"\n" +
                "  }\n" +
                "}"
)
public class VerificationAndReverifyUserEmail {
}
