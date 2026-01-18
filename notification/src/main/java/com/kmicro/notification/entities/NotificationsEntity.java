package com.kmicro.notification.entities;

import com.kmicro.notification.constansts.ChannelType;
import com.kmicro.notification.constansts.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationsEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_id")
    private Integer recipientId;

    @Column(name = "fragment_path")
    private String fragment;

    @Column(name = "send_to")
    private String sendTo;

    private String subject;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type")
    private ChannelType channelType;

    private Integer priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object>payload;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object>mailBody;

    @Column(columnDefinition = "TEXT", name = "fail_reason")
    private String failureReason;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt; // -- For delayed notifications

//    public Object getBodyFromPayload(){
//        return this.payload.get("body");
//    }
}
