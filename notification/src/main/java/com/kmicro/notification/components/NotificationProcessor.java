package com.kmicro.notification.components;

import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.repository.NotificationRepository;
import com.kmicro.notification.utils.CommonHelperUtils;
import com.kmicro.notification.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProcessor {

    private final NotificationRepository notificationRepository;
    private final EmailUtils emailUtils;
    private final CommonHelperUtils commonHelperUtils;

    @SchedulerLock(
            name = "NotificationProcessorTaskLock",
            lockAtMostFor = "15s",
            lockAtLeastFor = "5s"
    )
    @Scheduled(fixedDelay = 10000) // Runs every 10 seconds
    @Transactional
    public void processNotifications() {
        List<NotificationsEntity> pendingNotificationEvents = notificationRepository.findByStatus(Status.PENDING);

        for (NotificationsEntity entity : pendingNotificationEvents) {
            try {
                //--- Update Entity recipient_id,  retry_count, Status, updated_at
//                String frag = emailUtils.getFragment(event.getTemplateName());
//                Map<String, Object> bodyData = emailUtils.getDataMapFromContent(event.getBodyFromPayload());
                String html = emailUtils.getHtmlBodyFromContent(entity.getMailBody(), entity.getFragment());

                //--------- Chaos Monkey -------------
                commonHelperUtils.chaosMonkey(false);

                //--------- Chaos Monkey -------------
                emailUtils.sendMailAsync(entity.getId(), entity.getSendTo(),entity.getSubject(), html);
                entity.setStatus(Status.SENT);
                entity.setUpdatedAt(Instant.now());
                log.info("Mail Passed to MailSender Successfully");
            } catch (Exception e) {
                handleFailure(entity, e);
            }
        }
    }

    private void handleFailure(NotificationsEntity event, Exception e) {
        event.setRetryCount(event.getRetryCount() + 1);
        if (event.getRetryCount() > 5) {
            event.setStatus(Status.FAILED);
            event.setFailureReason(e.getMessage());
            event.setUpdatedAt(Instant.now());
        }
        log.error("Failure Cause: ", e);
        notificationRepository.save(event);
        log.error("Failed to publish outbox event {}", event.getId());
    }



}//EC
