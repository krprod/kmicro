package com.kmicro.notification.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kmicro.notification.dtos.KafkaEventRec;
import com.kmicro.notification.dtos.MailRequestRec;
import com.kmicro.notification.dtos.RequestedJsonRecord;
import com.kmicro.notification.service.EmailService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
@Validated
@Tag(name = "Email Controller", description = "Operations for Email Notification")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Hidden
    @PostMapping("/email")
    public ResponseEntity<String> sendEmail() {
        /*emailService.sendSimpleEmail(
                "x@gmail.com",
                "Test Subject",
                "This is the body of the email."
        );*/

        String html = """
                <h2 style="color:green;">Hello from Spring Boot</h2>
                <p>This is a <b>scheduled email</b> with HTML and attachment.</p>
            """;

//        emailService.sendHtmlEmailWithAttachment(
//                "x@gmail.com",
//                "Order Placed",
//                html,
//                "x.pdf"
//        );

        return ResponseEntity.ok("Email sent!");
    }

    @Operation(summary = "Send Simple Text Email Based On MailRequestRec DTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Started Processing"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MailRequestRec.class),
                    examples = {
                            @ExampleObject(
                                    name = "Standard Request Object",
                                    value = "{\n" +
                                            "    \"sendTo\":\"theMicro@gmail.com\",\n" +
                                            "    \"subject\":\"Test Mail 2026\",\n" +
                                            "    \"sendfrom\":\"\",\n" +
                                            "    \"body\":\"This is the test mail from notification service\",\n" +
                                            "    \"cc\":\"\",\n" +
                                            "    \"bcc\":\"\",\n" +
                                            "    \"attachementPath\":\"x.pdf\"\n" +
                                            "}"
                            ),
                      /*      @ExampleObject(
                                    name = "Bulk Order",
                                    summary = "An example of a large order",
                                    value = "{\"itemId\": 505, \"quantity\": 1000}"
                            )*/
                    }
            )
    )    @PostMapping("/simple")
    public ResponseEntity<String>sendSimpleMail(@Valid @RequestBody MailRequestRec requestRec){
        emailService.sendSimpleEmail(requestRec);
        return ResponseEntity.ok("Mail Sent Successfully");
    }

    @Operation(summary = "Send HTML Email Based On MailRequestRec DTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Started Processing"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MailRequestRec.class),
                    examples = {
                            @ExampleObject(
                                    name = "Standard Request Object",
                                    value = "{\n" +
                                            "    \"sendTo\":\"theWork@gmail.com\",\n" +
                                            "    \"subject\":\"Test Mail 2026\",\n" +
                                            "    \"sendfrom\":\"\",\n" +
                                            "    \"body\":\"{\\\"orderItems\\\":[{\\\"id\\\":3,\\\"quantity\\\":10,\\\"price\\\":100.25,\\\"product_id\\\":21,\\\"item_img\\\":\\\"/api/product/112/elf.png\\\",\\\"item_name\\\":\\\"hrishan\\\"},{\\\"id\\\":4,\\\"quantity\\\":10,\\\"price\\\":100.25,\\\"product_id\\\":11,\\\"item_img\\\":\\\"/api/product/112/elf.png\\\",\\\"item_name\\\":\\\"hrishan\\\"}],\\\"id\\\":2,\\\"user_id\\\":8,\\\"order_date\\\":\\\"2026-01-05 18:37:11.34\\\",\\\"order_status\\\":\\\"PENDING\\\",\\\"order_total\\\":2005,\\\"payment_method\\\":\\\"ONLINE\\\",\\\"transaction_id\\\":\\\"tranx_01\\\",\\\"payment_status\\\":\\\"PENDING\\\",\\\"shipping_address\\\":{\\\"city\\\":null,\\\"country\\\":null,\\\"address_id\\\":0,\\\"shipping_address\\\":null,\\\"zip_code\\\":null},\\\"tracking_number\\\":\\\"track_01\\\",\\\"frag\\\":\\\"fr_welcome\\\"}\",\n" +
                                            "    \"cc\":\"\",\n" +
                                            "    \"bcc\":\"\",\n" +
                                            "    \"attachementPath\":\"x.pdf\"\n" +
                                            "}"
                            ),
                      /*      @ExampleObject(
                                    name = "Bulk Order",
                                    summary = "An example of a large order",
                                    value = "{\"itemId\": 505, \"quantity\": 1000}"
                            )*/
                    }
            )
    )
    // multi-part mail
    @PostMapping("/multipart-mail")
    public ResponseEntity<String>sendMultiPartMail(@Valid @RequestBody MailRequestRec requestRec){
        emailService.sendMultiPartMail(requestRec);
        return ResponseEntity.ok("Mail Sent Successfully");
    }

    @Operation(summary = "Send Trigger Kafka Event send Email Based On KafkaEventRec Record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Started Processing"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = KafkaEventRec.class),
                    examples = {
                            @ExampleObject(
                                    name = "OTP Request",
                                    value = "{\"eventType\":\"otp\",\"sendto\":\"workmail@gmail.com\",\"subject\":\"Login OTP\",\"body\":{\"title\":\"Login OTP\",\"greetingByName\":\"Hi Krishna,\",\"msgLine1\":\"Use the code below to log in to your Udemy account.\",\"otpCode\":\"254860\"}}"
                            ),
                            @ExampleObject(
                                    name = "Password Reset",
                                    value = "{\"eventType\":\"passwordChange\",\"sendto\":\"workmail@gmail.com\",\"subject\":\"Password Reset!!\",\"body\":{\"title\":\"Password Reset!!\",\"token\":\"uCxvxvGro7iAvBb9pxRdTKVHDWrmH8dp6BXiJ9K7BA6L8d88WEDfGbR5HYVELeap\"}}"
                            ),
                            @ExampleObject(
                                    name = "Welcome To Kmicro",
                                    value = "{\"eventType\":\"newUser\",\"sendto\":\"workmail@gmail.com\",\"subject\":\"Welcome To Kmicro\",\"body\":{\"title\":\"Welcome To Kmicro\",\"userName\":\"Hi Krishna,\",\"msgLine1\":\"We're thrilled to have you here. To get you started, use the code below for 10% off your first order.\",\"discountCode\":\"WELCOME10\"}}"
                            ),
                            @ExampleObject(
                                    name = "Order Status Update Request",
                                    value = "{\n" +
                                            "    \"eventType\": \"orderStatusUpdate\",\n" +
                                            "    \"sendto\": \"workmail@gmail.com\",\n" +
                                            "    \"subject\": \"Order Status Update \",\n" +
                                            "    \"body\":{\n" +
                                            "    \"title\":\"Order Status Update\",\n" +
                                            "      \"statusTitle\": \"Your order is on its way!\",\n" +
                                            "      \"statusMessage\": \"Great news! Your package has been handed over to the courier.\",\n" +
                                            "        \"trackingNumber\": \"TRK123456789\",\n" +
                                            "        \"carrierName\":\"FedEx\",\n" +
                                            "            \"trackingUrl\": \"httssf//asdfjlsadf.saf\"\n" +
                                            "}\n" +
                                            "}"
                            ),
                            @ExampleObject(
                                    name = "Order Confirmation Request",
                                    value = "{\n" +
                                            "    \"eventType\": \"orderConfirm\",\n" +
                                            "    \"sendTo\": \"workmail@gmail.com\",\n" +
                                            "    \"subject\": \"Order Confirmation\",\n" +
                                            "    \"body\": {\n" +
                                            "        \"title\": \"Order Confirmation\",\n" +
                                            "        \"greetingByName\": \"Hi Krishna, your order is confirmed!\",\n" +
                                            "        \"msgLine1\": \"Thank you for shopping with us. Your order details are below.\",\n" +
                                            "        \"items\": [\n" +
                                            "            {\n" +
                                            "                \"id\": 3,\n" +
                                            "                \"quantity\": 10,\n" +
                                            "                \"price\": 100.25,\n" +
                                            "                \"product_id\": 21,\n" +
                                            "                \"img\": \"https://image01.realme.net/general/20211011/1633945684839.png\",\n" +
                                            "                \"link\": \"https://api/product/112\",\n" +
                                            "                \"name\": \"acer SmartChoice Aspire Lite, AMD Ryzen 5-5625U Processor, 16 GB/512 GB, Full HD, 15.6\\\"/39.62 cm, Windows 11 Home, Steel Gray, 1.59 kg, AL15-41, Metal Body, Thin and Light Laptop\"\n" +
                                            "            },\n" +
                                            "            {\n" +
                                            "                \"id\": 3,\n" +
                                            "                \"quantity\": 10,\n" +
                                            "                \"price\": 100.25,\n" +
                                            "                \"product_id\": 21,\n" +
                                            "                \"img\": \"https://image01.realme.net/general/20211011/1633945684839.png\",\n" +
                                            "                \"name\": \"Bose New SoundLink Flex Portable Bluetooth Speaker (2nd Gen), Portable Outdoor Speaker with Hi-Fi Audio, Up to 12 Hours Battery Life, Waterproof and Dustproof, Blue Dusk\",\n" +
                                            "                \"link\": \"https://api/product/112\"\n" +
                                            "            }\n" +
                                            "        ],\n" +
                                            "        \"details\": {\n" +
                                            "            \"name\": \"Ravidas Khan\",\n" +
                                            "            \"contact\": \"+91 9858754235\",\n" +
                                            "            \"email\": \"Ravidas@gmail.com\",\n" +
                                            "            \"city\": \"delhi\",\n" +
                                            "            \"country\": \"india\",\n" +
                                            "            \"address_id\": 0,\n" +
                                            "            \"shipping_address\": \"Karawal Nagar, Surya Vihar gali 4\",\n" +
                                            "            \"zip_code\": \"110094\"\n" +
                                            "        },\n" +
                                            "        \"trackingUrl\": \"httssf//asdfjlsadf.saf\",\n" +
                                            "        \"totaling\": {\n" +
                                            "            \"totalPrice\": \"3000\",\n" +
                                            "            \"subtotal\": \"2900\",\n" +
                                            "            \"shippingFee\": \"100\"\n" +
                                            "        }\n" +
                                            "    }\n" +
                                            "}"
                            )
                    }
            )
    )
    @PostMapping("/send-kafka-event")
    public ResponseEntity<String> sendKafkaEvent(@RequestBody KafkaEventRec eventRec){
        emailService.sendKafkaEvent(eventRec);
        return ResponseEntity.ok("Mail Sent Successfully");
    }


    // account login OTP mail
    @Hidden
    @PostMapping("/opt")
    public ResponseEntity<String>sendOTP(@Valid @RequestBody MailRequestRec requestRec){
        emailService.sendOTP(requestRec);
        return ResponseEntity.ok("Mail Sent Successfully");
    }

    @Hidden
    @PostMapping("/generic")
    public ResponseEntity<String>genericData(@RequestBody JsonNode reqObj){
        emailService.genericData(reqObj);
        return ResponseEntity.ok("Mail Sent Successfully");
    }

    @Hidden
    @PostMapping("/play")
    public ResponseEntity<String>orderConfirm(@RequestBody RequestedJsonRecord reqObj){
        emailService.orderConfirm(reqObj);
        return ResponseEntity.ok("Mail Sent Successfully");
    }
    // order status update mail

    // new order request mail
}