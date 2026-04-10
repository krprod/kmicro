package com.kmicro.notification.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record KafkaEventRec(
        @Schema(description = "Few Fixed Event Types", example = "orderConfirm,orderStatusUpdate,newUser, passwordChange, otp") String eventType,
        String sendto,
        String subject,
        String body
        ){ }
