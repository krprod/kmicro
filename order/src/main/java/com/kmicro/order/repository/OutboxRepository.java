package com.kmicro.order.repository;

import com.kmicro.order.entities.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {

    List<OutboxEntity> findByStatus(String status);

    @Modifying
    @Query("DELETE FROM OutboxEntity e WHERE e.status = 'PROCESSED' AND e.createdAt > :threshold")
    int deleteProcessedOlderThan(LocalDateTime threshold);

}
