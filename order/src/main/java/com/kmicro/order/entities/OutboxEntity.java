package com.kmicro.order.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events"
        // Create composite index
        /*,indexes = {
        @Index(name = "idx_outbox_status_created", columnList = "status, createdAt")
        }*/
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String eventType;
    private String sourceSystem;
    private String aggregateId; // e.g., Order ID

    @Column(columnDefinition = "TEXT")
    private String payload; // Your JSON String

    private int retryCount = 0;
    private String status = "PENDING"; // PENDING, PROCESSED, FAILED

    private LocalDateTime createdAt = LocalDateTime.now();

}
