package com.kmicro.notification.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.notification.constansts.AppConstants;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.constansts.Templates;
import com.kmicro.notification.dtos.MailRequestRec;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtils {

    private final NotificationDBUtils notificationDBUtils;
    private final MockEmaiData mockEmaiData;
    private final TemplateEngine templateEngine;
    private final ContextCreatorUtils contextCreatorUtils;
    private final MailGenerator mailGenerator;
    private final ObjectMapper objectMapper;
    private final CommonHelperUtils commonHelperUtils;

    public SimpleMailMessage createSimpleTextMail(MailRequestRec requestRec){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(requestRec.sendTo());
        message.setFrom(requestRec.sendfrom() == "" ? "no-reply.xyz.com" : requestRec.sendfrom());
        message.setSubject(requestRec.subject());
        message.setText(requestRec.body());
        message.setReplyTo(requestRec.sendfrom() == "" ? "no-reply.xyz.com" : requestRec.sendfrom());
        message.setSentDate(Date.from(Instant.now().atZone(ZoneId.of(AppConstants.ASIA_TIME_ZONE)).toInstant()));

        log.info("SimpleMailMessage Generated Successfully for Request: {}", requestRec);
        return message;
    }

    public MimeMessage createMultiPartMail(MailRequestRec requestRec) {
        try {
            var message = mailGenerator.getMessage();
            var helper = mailGenerator.getMimeHelper(message);
            mailGenerator.setRequiredFields(helper, requestRec);

            Map<String, Object> mailBodyMap = commonHelperUtils.getDataMapFromContent(requestRec.body());
            Context context =  contextCreatorUtils.getNewContextForMap(mailBodyMap);
            String html =  this.prepareHtmlBody(context, this.getFragment(mailBodyMap.get("frag").toString()));

            mailGenerator.setHtml(helper,html);
            mailGenerator.sendMultiPartMail(message);
            /*   FileSystemResource file = mockEmaiData.getMockFile(requestRec.attachementPath());
                    log.info("Sending File: {} from Path: {}",file.getFilename(), file.getURL());
                    helper.addAttachment(file.getFilename(), file);*/
            log.info("MimeMessage Generated Successfully for Request: {}", requestRec.toString());
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String prepareHtmlBody(Context cntxt, String fragment) {
        try {
            log.info("Processing Template For: {}",cntxt.getVariable("title"));
            contextCreatorUtils.setFragment(cntxt, fragment);
         return  templateEngine.process(Templates.DEFAULT_LAYOUT.getName(), cntxt);
        } catch (Exception e) {
            log.error("TemplateEngine Processing Failed", e);
            throw  new RuntimeException(e);
        }

    }

    public String getFragment(String fragName){
//        if(fragName.isBlank())
        String frag = null;
        if(Templates.VERIFY_OTP.toString().equalsIgnoreCase(fragName)){
            frag =  Templates.VERIFY_OTP.getName();
        }
        else if (Templates.FR_WELCOME.toString().equalsIgnoreCase(fragName)) {
            frag = Templates.FR_WELCOME.getName();
        }
        else if (Templates.FR_SHIPPING_UPDATE.toString().equalsIgnoreCase(fragName)) {
            frag = Templates.FR_SHIPPING_UPDATE.getName();
        }
        else if (Templates.FR_SECURITY_ALERT.toString().equalsIgnoreCase(fragName)) {
            frag = Templates.FR_SECURITY_ALERT.getName();
        }
        else if (Templates.FR_PAYMENT_FAIL.toString().equalsIgnoreCase(fragName)) {
            frag = Templates.FR_PAYMENT_FAIL.getName();
        }
        else if (Templates.FR_ORDER_CONFIRM.toString().equalsIgnoreCase(fragName)) {
            frag = Templates.FR_ORDER_CONFIRM.getName();
        }
        else if (Templates.FR_OPT_VERIFICATION.toString().equalsIgnoreCase(fragName)) {
            frag = Templates.FR_OPT_VERIFICATION.getName();
        }
        else if (Templates.FR_ABANDON_CART.toString().equalsIgnoreCase(fragName)) {
            frag = Templates.FR_ABANDON_CART.getName();
        }
        return frag;
    }

    public  <T> String getHtmlBodyFromContent(T data, String fragment) {
        Map<String, Object> dataMap = commonHelperUtils.getDataMapFromContent(data);
        Context context =  contextCreatorUtils.getNewContextForMap(dataMap);
        return  prepareHtmlBody(context, fragment);
    }

    public void sendMailAsync(MailRequestRec requestRec) {
        try {
            String frag = this.getFragment(Templates.FR_ORDER_CONFIRM.name());
            String html  = this.getHtmlBodyFromContent(requestRec.body(), frag);

            var message = mailGenerator.getMessage();
            var helper = mailGenerator.getMimeHelper(message);
            mailGenerator.setRequiredFields(helper,requestRec);
            mailGenerator.setHtml(helper, html);
            mailGenerator.sendMultiPartMail(message);

//             mailGenerator.doMailWa(html,requestRec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMail(MailRequestRec requestRec) {
        try {
            String frag = this.getFragment(Templates.FR_ORDER_CONFIRM.name());
            String html  = this.getHtmlBodyFromContent(requestRec.body(), frag);

            var message = mailGenerator.getMessage();
            var helper = mailGenerator.getMimeHelper(message);
            mailGenerator.setRequiredFields(helper,requestRec);
            mailGenerator.setHtml(helper, html);
            mailGenerator.sendMultiPartMail(message);

//             mailGenerator.doMailWa(html,requestRec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Async("emailExecutor")
    @Retryable(
            retryFor = { MailException.class, MessagingException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000, multiplier = 2)
    )
    public void sendMailAsync(UUID notificationID, String sendto, String subject, String html) throws MessagingException {
//        try{
            var message = mailGenerator.getMessage();
            var helper = mailGenerator.getMimeHelper(message);
//            mailGenerator.setRequiredFields(helper,requestRec);
            helper.setReplyTo(AppConstants.NO_REPLY_MAIL);
            helper.setFrom(AppConstants.NO_REPLY_MAIL);

            helper.setSubject(subject);
            helper.setTo(sendto);
            mailGenerator.setHtml(helper, html);
        System.out.println(html);
            log.info("------------   ALL SET, INITIATING MAIL SENDER -------------------");
            mailGenerator.sendMultiPartMail(message);
            notificationDBUtils.updateDeliveryStatus(notificationID, Status.DELIVERED);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    @Recover
    public void recover(Exception e, UUID notificationID, String sendto, String subject, String html ) {
        // Logic for total failure: Save to a 'failed_emails' DB table or alert DevOps
        log.error("PERMANENT EMAIL FAILURE: Could not send {} to {}. Manual intervention required. For ID: {}", subject, sendto, notificationID);
        notificationDBUtils.updateDeliveryStatus(notificationID, Status.PERMANENT_FAILURE, e);
        log.error("EXCEPTION: ", e);
    }


    /* public  Map<String, Object> getMapObjectFromJsonNode(JsonNode data){
        return objectMapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
    }*/

   /* public <T> Map<String, Object> createMapObject(T data){
        return objectMapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
    }*/

    /*public  Map<String, Object> getMapObjectFromString(String data){
        try {
            JsonNode node = objectMapper.readTree(data);
            return getMapObjectFromJsonNode(node);
        }catch (JacksonException e) {
            log.error("Casting String Msg to JsonNode Failed", e);
            throw new RuntimeException(e);
        }
    }*/

    /*    public  <T> Map<String, Object> getDataMapFromContent(T data){
            Map<String, Object> dataMap = new HashMap<>();
            if(data instanceof String d1){
                dataMap = this.getMapObjectFromString(d1);
            } else if (data instanceof JsonNode d2) {
                dataMap = this.getMapObjectFromJsonNode(d2);
            }else{
                dataMap = this.createMapObject(data);
            }
            log.info("DataMapFromContent Generated Successfully");
            return dataMap;
        }*/
}//EC
