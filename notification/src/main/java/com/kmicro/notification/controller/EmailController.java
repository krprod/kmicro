package com.kmicro.notification.controller;

import com.kmicro.notification.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
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

        emailService.sendHtmlEmailWithAttachment(
                "x@gmail.com",
                "Order Placed",
                html,
                "x.pdf"
        );

        return ResponseEntity.ok("Email sent!");
    }
}