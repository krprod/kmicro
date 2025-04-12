package com.kmicro.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class EmailService {

    @Autowired
        private JavaMailSender mailSender;

    @Autowired
    private  EmailServiceHelper emailServiceHelper;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${ATTACHMENT_PATH}")
    private String attachment;

    @Value("${TESTUSER}")
    private String testUser;

        public void sendSimpleEmail(String to, String subject, String body) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("vatsal.ffnf@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }

        public  void sendHtmlEmailWithAttachment(String to,  String subject,  String htmlBody,  String attachmentPath){
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8"); // true = multipart

                helper.setFrom(username);
                helper.setTo(testUser);
                helper.setSubject(subject);
               String html = emailServiceHelper.createCntx(emailServiceHelper.getOrderDetails());
                helper.setText(html, true); // true = HTML

                emailServiceHelper.addAttachment(helper,attachmentPath);
//                log.info("username: {}, testUser: {}, attachmentPath: {}",username,testUser,attachment);
//               mailSender.send(message);
            }catch (Exception e) {
                log.info("error while sending mail:{}",e.getMessage());
                log.info("detail error msg: {}",e.getStackTrace());
            }
        }

}
