package com.kmicro.notification.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example = "{\n" +
                "  \"notification_id\":\"\",\n" +
                "  \"user_id\":\"\",\n" +
                "  \"name\": \"Ravidas Khan\",\n" +
                "  \"contact\": \"+91 9858754235\",\n" +
                "  \"email\": \"Ravidas@gmail.com\",\n" +
                "  \"city\": \"delhi\",\n" +
                "  \"state\": \"delhi\",\n" +
                "  \"country\": \"india\",\n" +
                "  \"address_id\": 0,\n" +
                "  \"shipping_address\": \"Karawal Nagar, Surya Vihar gali 4\",\n" +
                "  \"zip_code\": \"110094\"\n" +
                "}"
)
public class SharedUserDetails{
}
