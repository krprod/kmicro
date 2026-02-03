package com.kmicro.user.component;

import com.kmicro.user.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxCleanupJob {

    private final OutboxRepository outboxRepository;

    // Runs once every hour (or use a CRON expression for off-peak hours)
//    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(
            name = "OutboxCleanupJobTaskLock",
            lockAtMostFor = "15s",
            lockAtLeastFor = "5s"
    )
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void cleanupProcessedEvents() {
//        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        Instant threshold =Instant.now().minus(1, ChronoUnit.DAYS);
        try {
           log.info("Starting cleanup of processed outbox events older than {}", threshold);

           // Custom query to delete in bulk
           int deletedCount = outboxRepository.deleteProcessedOlderThan(threshold);

           log.info("Cleanup finished. Removed {} processed events.", deletedCount);
       } catch (Exception e) {
           log.info("Cleanup Failed for outbox events older than {}", threshold);
           throw new RuntimeException(e);
       }
    }


}
