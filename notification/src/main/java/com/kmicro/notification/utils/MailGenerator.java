package com.kmicro.notification.utils;

import com.kmicro.notification.constansts.AppConstants;
import com.kmicro.notification.dtos.MailRequestRec;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailGenerator {
    private final JavaMailSender javaMailSender;
    private final CommonHelperUtils commonHelperUtils;

    public MimeMessage getMessage(){
        log.debug("MimeMessage Created");
        return javaMailSender.createMimeMessage();
    }

    public MimeMessageHelper getMimeHelper(MimeMessage message) throws MessagingException {
        log.debug("MimeMessageHelper Created");
           return new MimeMessageHelper(
                   message,
                   true,
                   "UTF-8");
    }

    public void setRequiredFields(MimeMessageHelper helper, MailRequestRec requestRec) throws MessagingException {
        String sendFrom = requestRec.sendfrom().equalsIgnoreCase("")  ? AppConstants.NO_REPLY_MAIL  : requestRec.sendfrom();
        helper.setFrom(sendFrom);
        helper.setReplyTo(sendFrom);

        helper.setTo(requestRec.sendTo());
        helper.setSubject(requestRec.subject());

        log.info("Setting Mail Required Fields");
    }

    public void setHtml(MimeMessageHelper helper, String html) throws MessagingException{
        helper.setText(html, true);
        log.info("Setting Mail HTML");
    }

    public void setFile(MimeMessageHelper helper,String attachmentPath) throws MessagingException, IOException {
        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        log.info("Attaching File: {} from Path: {}",file.getFilename(), file.getURL());
        helper.addAttachment(file.getFilename(), file);
    }

    @Async("emailExecutor")
    public void sendSimpleMail(SimpleMailMessage mailMessage){
        javaMailSender.send(mailMessage);
        log.info("Simple Mail Sent Successfully");
    }


    public void sendMultiPartMail(MimeMessage mailMessage){
        try {
//            javaMailSender.send(mailMessage);
            commonHelperUtils.chaosMonkey(false);
            log.info("Multi-Part Mail Sent Successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getTextFromMessage(MimeMessage message) throws Exception {
        message.saveChanges();
        Object content = message.getContent();

        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof MimeMultipart) {
            return getTextFromMimeMultipart((MimeMultipart) content);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                return bodyPart.getContent().toString(); // Prefer plain text
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(bodyPart.getContent().toString());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    public void doMailWa(String html, MailRequestRec requestRec){
        try {
            var message = javaMailSender.createMimeMessage();
            var helper = getMimeHelper(message);
            helper.setFrom(requestRec.sendfrom() == "" ? "no-reply.xyz.com" : requestRec.sendfrom());
            helper.setTo(requestRec.sendTo());
            helper.setSubject(requestRec.subject());
            helper.setReplyTo(requestRec.sendfrom() == "" ? "no-reply.xyz.com" : requestRec.sendfrom());
            helper.setText(html, true);
            sendMultiPartMail(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

   /* public void chaosMonkey(Boolean enabled){
        if(!enabled) return;
        try {
            Thread.sleep(5000);
            double  failureProbability = 0.5;
            double chance = ThreadLocalRandom.current().nextDouble();
            if (chance < failureProbability) {
                log.error("❌ RANDOM ERROR: Chaos Monkey triggered! (Chance: {} < Probability: {})",
                        String.format("%.2f", chance), failureProbability);

                throw  new RuntimeException(new MessagingException("Meri Marzi"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //            log.info("✅ SUCCESS: Operation completed. (Chance: {} >= Probability: {})",
//                    String.format("%.2f", chance), failureProbability);
//            String Text = getTextFromMessage(mailMessage);
//           log.info("Text: {}",Text);
    }*/
}//EC
