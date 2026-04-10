package com.kmicro.notification.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( example = "{\n" +
        "  \"sendto\": \"sadf@gmail.com\",\n" +
        "  \"subject\": \"Order Confirmation\",\n" +
        "  \"body\": {\n" +
        "    \"title\": \"Order Confirmation\",\n" +
        "    \"greetingByName\": \"Hi Krishna, your order is confirmed!\",\n" +
        "    \"msgLine1\": \"Thank you for shopping with us. Your order details are below.\",\n" +
        "    \"items\": [\n" +
        "      {\n" +
        "        \"id\": 3,\n" +
        "        \"quantity\": 10,\n" +
        "        \"price\": 100.25,\n" +
        "        \"product_id\": 21,\n" +
        "        \"img\": \"https://kmicro/product/general/20211011/1633945684839.png\",\n" +
        "        \"link\": \"https://kmicro/product/112\",\n" +
        "        \"name\": \"acer SmartChoice Aspire Lite, AMD Ryzen 5-5625U Processor Windows 11 Home, Steel Gray, 1.59 kg, AL15-41, Metal Body, Thin and Light Laptop\"\n" +
        "      },\n" +
        "      {\n" +
        "        \"id\": 3,\n" +
        "        \"quantity\": 10,\n" +
        "        \"price\": 100.25,\n" +
        "        \"product_id\": 21,\n" +
        "        \"img\": \"https://kmicro/product/general/20211011/1633945684839.png\",\n" +
        "        \"name\": \"Bose New SoundLink Flex Portable Bluetooth Speaker (2nd Gen), Portable Outdoor Speaker with Hi-Fi Audio, Up to 12 Hours Battery Life, Waterproof and Dustproof, Blue Dusk\",\n" +
        "        \"link\": \"https://kmicro/product/112\"\n" +
        "      }\n" +
        "    ],\n" +
        "    \"details\": {\n" +
        "      \"name\": \"Ravidas Khan\",\n" +
        "      \"contact\": \"+91 9858754235\",\n" +
        "      \"email\": \"Ravidas@gmail.com\",\n" +
        "      \"city\": \"delhi\",\n" +
        "      \"country\": \"india\",\n" +
        "      \"address_id\": 0,\n" +
        "      \"shipping_address\": \"Karawal Nagar, Surya Vihar gali 4\",\n" +
        "      \"zip_code\": \"110094\"\n" +
        "    },\n" +
        "    \"trackingUrl\": \"httssf//kmicro/product/track/20211011\",\n" +
        "    \"totaling\": {\n" +
        "      \"totalPrice\": \"3000\",\n" +
        "      \"subtotal\": \"2900\",\n" +
        "      \"shippingFee\": \"100\"\n" +
        "    }\n" +
        "  }\n" +
        "}")
public class OrderConfirmed {
}
