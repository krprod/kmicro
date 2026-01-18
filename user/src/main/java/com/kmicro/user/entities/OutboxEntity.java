package com.kmicro.user.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users_outbox_events")
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
    private String sourceSystem;
    private String aggregateId;

    @Column(columnDefinition = "TEXT")
    private String payload; // Your JSON String

    private int retryCount = 0;
    private String status = "PENDING"; // PENDING, PROCESSED, FAILED

    private LocalDateTime createdAt = LocalDateTime.now();

}
