package com.kmicro.order.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example = "{  \"payment_id\": 252,\n" +
                "  \"order_id\": 502,\n" +
                "  \"transaction_id\": \"pay_YKdf6OjxYkcsaq\",\n" +
                "  \"user_id\": 1,\n" +
                "  \"payment_status\": \"SUCCESS\",\n" +
                "  \"amount\": 2155.85\n" +
                "}"
)
public class PaymentResponseSchema {
}
