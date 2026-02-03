package com.kmicro.user.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example = "{\n" +
                "    \"user_id\":\"1\",\n" +
                "    \"address_id\":\"10\",\n" +
                "    \"notification_id\":\"916e1396-2579-47d1-9518-421478f205fb\"\n" +
                "}"
)
public class RequestUserDetail {
}
