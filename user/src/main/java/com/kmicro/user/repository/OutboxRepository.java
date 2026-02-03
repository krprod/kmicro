package com.kmicro.user.repository;

import com.kmicro.user.entities.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findByStatus(String status);

    Boolean existsByAggregateId(String aggID);

    Optional<OutboxEntity> findByAggregateId(String aggID);

    @Modifying
    @Query("DELETE FROM OutboxEntity e WHERE e.status = 'PROCESSED' AND e.createdAt > :threshold")
    int deleteProcessedOlderThan(Instant threshold);

}
