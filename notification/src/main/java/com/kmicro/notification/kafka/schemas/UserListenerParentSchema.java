package com.kmicro.notification.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

//@Schema(oneOf = {VerificationAndReverifyUserEmail.class, WelcomeUserMail.class,SharedUserDetails.class})
@Schema(
        example = "//--- Email Verification ---------\n" +
                "{\n" +
                "  \"sendto\": \"jheengaKumar1@gmail.com\",\n" +
                "  \"userData\": {\n" +
                "    \"login_name\": \"jheengaKumar1\",\n" +
                "    \"user_id\": 1,\n" +
                "    \"email\": \"jheengaKumar1@gmail.com\"\n" +
                "  },\n" +
                "  \"subject\": \"Welcome To Kmicro\",\n" +
                "  \"body\": {\n" +
                "    \"greetingByName\": \"Hi ramaKrishna1\",\n" +
                "    \"verifyLink\": \"http://localhost:8085/api/auth/verify?token=jjjUPQIOncdzBKsFs_Rn8JZnDOZYfKM2-PbTXdXklv0\",\n" +
                "    \"title\": \"Verify Your Email\",\n" +
                "    \"msgLine1\": \"Click on the below link button to verify your email address or Copy/Paste the link in browser\"\n" +
                "  }\n" +
                "}\n" +
                "//---- Welcome User ---------\n" +
                "{\n" +
                "  \"sendto\": \"jheengaKumar1@gmail.com\",\n" +
                "  \"userData\": {\n" +
                "    \"login_name\": \"jheengaKumar1\",\n" +
                "    \"user_id\": 1,\n" +
                "    \"contact\": \"59874631288\",\n" +
                "    \"name\": \"jheenga kumar\",\n" +
                "    \"email\": \"jheengaKumar1@gmail.com\"\n" +
                "  },\n" +
                "  \"subject\": \"Welcome To Kmicro\",\n" +
                "  \"body\": {\n" +
                "    \"discountCode\": \"WELCOME100\",\n" +
                "    \"title\": \"Welcome To Kmicro\",\n" +
                "    \"userName\": \"Jheenga kumar Singh\",\n" +
                "    \"msgLine1\": \"We're thrilled to have you here. To get you started, use the code below for 10% off your first order.\"\n" +
                "  }\n" +
                "}\n" +
                "//--- User Shared ---------\n" +
                "{\n" +
                "\t\"address\": {\n" +
                "\t\t\"country\": \"India\",\n" +
                "\t\t\"user_id\": 1,\n" +
                "\t\t\"city\": \"chagganVihar\",\n" +
                "\t\t\"address_id\": 52,\n" +
                "\t\t\"state\": \"delhi\",\n" +
                "\t\t\"shipping_address\": \"the address line 1 the address line 2\",\n" +
                "\t\t\"zip_code\": \"11065\"\n" +
                "\t},\n" +
                "\t\"userData\": {\n" +
                "\t\t\"login_name\": \"ramaKrishna1\",\n" +
                "\t\t\"user_id\": 1,\n" +
                "\t\t\"contact\": \"59874631288\",\n" +
                "\t\t\"name\": \"ramaKrishna\",\n" +
                "\t\t\"email\": \"ramaKrishna1@gmail.com\"\n" +
                "\t},\n" +
                "\t\"notification_id\": \"2d965cea-c31f-491b-a930-3684aed541ed\"\n" +
                "}"
)
public class UserListenerParentSchema {
}
