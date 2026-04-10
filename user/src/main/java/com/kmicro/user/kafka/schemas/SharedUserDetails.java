package com.kmicro.user.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example ="{\n" +
                "                          \"notification_id\": \"916e1396-2579-47d1-9518-421478f205fb\",\n" +
                "                          \"userData\": {\n" +
                "                            \"login_name\": \"userLoginName\",\n" +
                "                            \"user_id\": 6,\n" +
                "                            \"contact\": \"59874631288\",\n" +
                "                            \"name\": \"userName\",\n" +
                "                            \"email\": \"userEmail@mail.com\"\n" +
                "                          },\n" +
                "                          \"address\": {\n" +
                "                            \"country\": \"India\",\n" +
                "                            \"city\": \"delhi\",\n" +
                "                            \"address_id\": 52,\n" +
                "                            \"state\": \"delhi\",\n" +
                "                            \"shipping_address\": \"the address line 1 the address line 2\",\n" +
                "                            \"zip_code\": \"11065\"\n" +
                "                          }\n" +
                "                        }"
)
public class SharedUserDetails {
}
