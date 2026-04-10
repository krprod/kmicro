package com.kmicro.notification.mapper;

import com.kmicro.notification.constansts.ChannelType;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.entities.NotificationsEntity;

import java.time.Instant;
import java.util.Map;

public  class NotificationsMapper {

        public static NotificationsEntity mapToEntity(Map<String, Object> data, Map<String, Object> mailBody, String frag){
            NotificationsEntity entity = new NotificationsEntity();
            data.get("body");
            entity.setCreatedAt(Instant.now());
            entity.setUpdatedAt(Instant.now());
            entity.setChannelType(ChannelType.EMAIL);
            entity.setStatus(Status.PENDING);
            entity.setRetryCount(0);
            entity.setPriority(10);
            // --- Dynamic
            entity.setFragment(frag);
            entity.setMailBody(mailBody);
//            entity.setPayload(data);
            return  entity;
        }

        public  static void addFixedFields(NotificationsEntity entity){
            entity.setCreatedAt(Instant.now());
            entity.setUpdatedAt(Instant.now());
            entity.setChannelType(ChannelType.EMAIL);
            entity.setStatus(Status.PENDING);
            entity.setRetryCount(0);
            entity.setPriority(10);
//            return  entity;
        }
}
