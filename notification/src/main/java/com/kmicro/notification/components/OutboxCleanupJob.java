package com.kmicro.notification.components;

import com.kmicro.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxCleanupJob {

    private final NotificationRepository notificationRepository;

    // Runs once every hour (or use a CRON expression for off-peak hours)
//    @Scheduled(cron = "0 0 * * * *")
//    @Scheduled(cron = "0 */10 * * * *")
//    @SchedulerLock(
//            name = "importantTaskLock",
//            lockAtMostFor = "10m",
//            lockAtLeastFor = "1m"
//    )
//    @Transactional
    public void cleanupProcessedEvents() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        try {
           log.info("Starting cleanup of processed outbox events older than {}", threshold);

           // Custom query to delete in bulk
//           int deletedCount = notificationRepository.deleteProcessedOlderThan(threshold);

//           log.info("Cleanup finished. Removed {} processed events.", deletedCount);
       } catch (Exception e) {
           log.info("Cleanup Failed for outbox events older than {}", threshold);
           throw new RuntimeException(e);
       }
    }


}
