package com.kmicro.notification.dtos;

import com.kmicro.notification.constansts.ChannelType;
import com.kmicro.notification.constansts.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificiationDTO {

    private UUID Id;
    private String recipientName;
    private Integer recipientId;
    private String fragment;
    private String sendTo;
    private String subject;
    private Integer retryCount;
    private ChannelType channelType;
    private Integer priority;
    private Status status;
    private Map<String, Object> payload;
    private Map<String, Object>mailBody;
    private String failureReason;
    private LocalDateTime scheduledAt; // -- For delayed notifications
    private Instant createdAt;
    private Instant updatedAt;
}
