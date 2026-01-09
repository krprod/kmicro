package com.kmicro.notification.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kmicro.notification.dtos.MailRequestRec;
import com.kmicro.notification.dtos.RequestedJsonRecord;
import com.kmicro.notification.service.EmailService;
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
public class EmailController {

    @Autowired
    private EmailService emailService;

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

    // simple  mail
    @PostMapping("/simple")
    public ResponseEntity<String>sendSimpleMail(@Valid @RequestBody MailRequestRec requestRec){
        emailService.sendSimpleEmail(requestRec);
        return ResponseEntity.ok("Mail Sent Successfully");
    }

    // multi-part mail
    @PostMapping("/multipart-mail")
    public ResponseEntity<String>sendMultiPartMail(@Valid @RequestBody MailRequestRec requestRec){
        emailService.sendMultiPartMail(requestRec);
        return ResponseEntity.ok("Mail Sent Successfully");
    }

    // email ID verfication mail

    // account login OTP mail
    @PostMapping("/opt")
    public ResponseEntity<String>sendOTP(@Valid @RequestBody MailRequestRec requestRec){
        emailService.sendOTP(requestRec);
        return ResponseEntity.ok("Mail Sent Successfully");
    }

    @PostMapping("/generic")
    public ResponseEntity<String>genericData(@RequestBody JsonNode reqObj){
        emailService.genericData(reqObj);
        return ResponseEntity.ok("Mail Sent Successfully");
    }

    @PostMapping("/play")
    public ResponseEntity<String>orderConfirm(@RequestBody RequestedJsonRecord reqObj){
        emailService.orderConfirm(reqObj);
        return ResponseEntity.ok("Mail Sent Successfully");
    }
    // order status update mail

    // new order request mail
}