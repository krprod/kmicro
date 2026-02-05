package com.kmicro.notification.kafka.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

//@Schema(oneOf = {OrderConfirmed.class})
@Schema(
        example = "//--- Order Confirm -----\n" +
                "{\n" +
                "  \"sendto\": \"TEMPA-1\",\n" +
                "  \"subject\": \"Order Confirmed\",\n" +
                "  \"body\": {\n" +
                "    \"greetingByName\": \"Hi, TEMPA-1,  your order is confirmed!\",\n" +
                "    \"trackingUrl\": \"https://kmicro.com/track/track_01\",\n" +
                "    \"totaling\": {\n" +
                "      \"shippingFee\": 100.0,\n" +
                "      \"totalPrice\": 2155.85,\n" +
                "      \"subtotal\": 2055.85\n" +
                "    },\n" +
                "    \"details\": {\n" +
                "      \"country\": \"India\",\n" +
                "      \"city\": \"Delhi\",\n" +
                "      \"contact\": \"TEMPA\",\n" +
                "      \"name\": \"TEMPA\",\n" +
                "      \"address_id\": 52,\n" +
                "      \"shipping_address\": \"Gali no 2, suray vihar\",\n" +
                "      \"email\": \"TEMPA\",\n" +
                "      \"zip_code\": \"1122554\"\n" +
                "    },\n" +
                "    \"title\": \"Order Confirmed\",\n" +
                "    \"msgLine1\": \"Thank you for shopping with us. Your order details are below.\",\n" +
                "    \"items\": [\n" +
                "      {\n" +
                "        \"img\": \"https://kmicro.com/product//api/product/112/elf.png\",\n" +
                "        \"quantity\": 2,\n" +
                "        \"price\": 700.8,\n" +
                "        \"product_id\": 12,\n" +
                "        \"link\": \"https://kmicro.com/product/12\",\n" +
                "        \"name\": \"hrishan\",\n" +
                "        \"id\": 102\n" +
                "      },\n" +
                "      {\n" +
                "        \"img\": \"https://kmicro.com/product//api/product/112/elf.png\",\n" +
                "        \"quantity\": 1,\n" +
                "        \"price\": 654.25,\n" +
                "        \"product_id\": 11,\n" +
                "        \"link\": \"https://kmicro.com/product/11\",\n" +
                "        \"name\": \"hrishan\",\n" +
                "        \"id\": 103\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}\n" +
                "//--- Order Confirm -----\n" +
                "{\n" +
                "  \"sendto\": \"dhruvworkmail07@gmail.com\",\n" +
                "  \"subject\": \"Order Status Update\",\n" +
                "  \"body\": {\n" +
                "    \"title\": \"Order Status Update\",\n" +
                "    \"statusTitle\": \"Your order is on its way!\",\n" +
                "    \"statusMessage\": \"Great news! Your package has been handed over to the courier.\",\n" +
                "    \"trackingNumber\": \"TRK123456789\",\n" +
                "    \"carrierName\": \"FedEx\",\n" +
                "    \"trackingUrl\": \"httssf//asdfjlsadf.saf\"\n" +
                "  }\n" +
                "}"
)
public class OrdersListenerParentSchema {
}
