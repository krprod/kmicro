package com.kmicro.notification.dtos;

import lombok.NonNull;

public record MailRequestRec(
        @NonNull  String sendTo,
        @NonNull  String subject,
        String sendfrom,
        String cc,
        String bcc,
        String body,
        String attachementPath) {
}
