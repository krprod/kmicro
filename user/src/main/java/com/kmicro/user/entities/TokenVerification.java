package com.kmicro.user.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "token_verification")
@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class TokenVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long  id;

    private String token;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "is_verified")
    private Boolean isVerified;
}
