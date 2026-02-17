package com.kmicro.order.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        example = "{\n" +
                "  \"sendto\": \"rajababu@gmail.com\",\n" +
                "  \"subject\": \"Payment Status Update\",\n" +
                "  \"body\": {\n" +
                "    \"payment\": {\n" +
                "      \"payment_id\": 52,\n" +
                "      \"order_id\": 52,\n" +
                "      \"transaction_id\": \"pay_DHjR4jbgX1cJra\",\n" +
                "      \"user_id\": 0,\n" +
                "      \"payment_status\": \"PAYMENT_FAILED\",\n" +
                "      \"amount\": 2062.75\n" +
                "    },\n" +
                "    \"totaling\": {\n" +
                "      \"totalPrice\": 2062.75,\n" +
                "      \"subtotal\": 1962.75,\n" +
                "      \"shippingFee\": 100.0\n" +
                "    },\n" +
                "    \"details\": {\n" +
                "      \"userData\": {\n" +
                "        \"name\": \"raja babu\",\n" +
                "        \"contact\": \"978967866\",\n" +
                "        \"email\": \"rajababu@gmail.com\"\n" +
                "      },\n" +
                "      \"address\": {\n" +
                "        \"country\": \"India\",\n" +
                "        \"city\": \"Delhi\",\n" +
                "        \"state\": \"Delhi\",\n" +
                "        \"address_id\": 52,\n" +
                "        \"shipping_address\": \"Gali no 2, suray vihar\",\n" +
                "        \"zip_code\": \"1122554\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"items\": [\n" +
                "      {\n" +
                "        \"id\": 52,\n" +
                "        \"quantity\": 2,\n" +
                "        \"price\": 654.25,\n" +
                "        \"product_id\": 13,\n" +
                "        \"img\": \"https://kmicro.com/product//api/product/13/elf.png\",\n" +
                "        \"link\": \"https://kmicro.com/product/13\",\n" +
                "        \"name\": \"hrishan\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 53,\n" +
                "        \"quantity\": 1,\n" +
                "        \"price\": 654.25,\n" +
                "        \"product_id\": 11,\n" +
                "        \"img\": \"https://kmicro.com/product//api/product/112/elf.png\",\n" +
                "        \"link\": \"https://kmicro.com/product/11\",\n" +
                "        \"name\": \"hrishan\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"title\": \"Payment Failed\",\n" +
                "    \"greetingByName\": \"Hi raja babu,We were unable to process your payment for Order <strong>#52</strong>. Don't worry, your items are still reserved.\",\n" +
                "    \"msgLine1\": \"Please update your payment method to complete the purchase. Last Payment details are below.\"\n" +
                "  }\n" +
                "}"
)
public class PaymentStatusUpdate {
}
