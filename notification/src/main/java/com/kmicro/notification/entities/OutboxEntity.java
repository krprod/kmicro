package com.kmicro.notification.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_outbox_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String topic;
    private String eventType;
    private String targetSystem;
    private String aggregateId;

    @Column(columnDefinition = "TEXT")
    private String payload; // Your JSON String

    private int retryCount = 0;
    private String status = "PENDING"; // PENDING, PROCESSED, FAILED

    private Instant createdAt = Instant.now();

}
