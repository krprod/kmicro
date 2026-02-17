package com.kmicro.order.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_events_seq")
    @SequenceGenerator(name = "outbox_events_seq", allocationSize = 50)
    private Long id;

    private String topic;
    private String eventType;

    @Column(name = "target_system")
    private String targetSystem;
    private String aggregateId; // e.g., Order ID

    @Column(columnDefinition = "TEXT")
    private String payload; // Your JSON String

    private int retryCount = 0;
    private String status = "PENDING"; // PENDING, PROCESSED, FAILED

    private Instant createdAt = Instant.now();

}
