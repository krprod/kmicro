package com.kmicro.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kmicro.notification.constansts.ChannelType;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.dtos.MailRequestRec;
import com.kmicro.notification.dtos.RequestedJsonRecord;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.repository.NotificationRepository;
import com.kmicro.notification.utils.EmailUtils;
import com.kmicro.notification.utils.MailGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailUtils emailUtils;
    private final MailGenerator mailGenerator;
    private final NotificationRepository notificationRepository;

      /*  public  void sendHtmlEmailWithAttachment(String to,  String subject,  String htmlBody,  String attachmentPath){
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
*/

        public void sendSimpleEmail(MailRequestRec requestRec) {
            mailGenerator.sendSimpleMail(
                    emailUtils.createSimpleTextMail(requestRec)
            );
        }

        public void sendMultiPartMail(MailRequestRec requestRec){
//            mailGenerator.sendMultiPartMail(
//                    emailUtils.createMultiPartMailHelper(requestRec)
//            );
        }


    public void sendOTP(MailRequestRec requestRec) {
            log.info("Regular Time: {}", LocalDateTime.now());
            emailUtils.sendMail(requestRec);
            log.info("Async Time: {}", LocalDateTime.now());
            emailUtils.sendMailAsync(requestRec);
        log.info("End Time: {}", LocalDateTime.now());
//        templateMix.append(emailUtils.prepareHtmlBody(requestRec, Templates.FR_ABANDON_CART.getName(),true))
//                .append(emailUtils.prepareHtmlBody(requestRec, Templates.FR_OPT_VERIFICATION.getName(),true))
//                .append(emailUtils.prepareHtmlBody(requestRec, Templates.FR_ORDER_CONFIRM.getName(),true))
//                .append(emailUtils.prepareHtmlBody(requestRec, Templates.FR_PAYMENT_FAIL.getName(),true))
//                .append(emailUtils.prepareHtmlBody(requestRec, Templates.FR_SECURITY_ALERT.getName(),true))
//                .append(emailUtils.prepareHtmlBody(requestRec, Templates.FR_SHIPPING_UPDATE.getName(),true))
//                .append(emailUtils.prepareHtmlBody(requestRec, Templates.FR_WELCOME.getName(),true));


    }

    public void genericData(JsonNode reqObj) {

    }

    public void orderConfirm(RequestedJsonRecord reqObj) {
            var data = emailUtils.getMapObjectFromJsonNode(reqObj.body());
        NotificationsEntity entity = new NotificationsEntity();
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        entity.setChannelType(ChannelType.EMAIL);
//        entity.setPriority(SmallIntJdbcType.INSTANCE.isSmallInteger());

        entity.setStatus(Status.PENDING);
        entity.setPayload(data);

        notificationRepository.save(entity);
        log.info("req: {}", reqObj);
    }
}//EC
