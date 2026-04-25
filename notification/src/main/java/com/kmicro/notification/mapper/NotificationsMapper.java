package com.kmicro.notification.mapper;

import com.kmicro.notification.constansts.ChannelType;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.dtos.NotificiationDTO;
import com.kmicro.notification.entities.NotificationsEntity;

import java.time.Instant;
import java.util.List;
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

        public static NotificiationDTO mapNotificationEntityToDTO(NotificationsEntity entity){
            NotificiationDTO notificiationDTO = new NotificiationDTO();
            notificiationDTO.setId(entity.getId());
            notificiationDTO.setRecipientId(entity.getRecipientId());
            notificiationDTO.setRecipientName(entity.getRecipientName());
            notificiationDTO.setFragment(entity.getFragment());
            notificiationDTO.setSendTo(entity.getSendTo());
            notificiationDTO.setSubject(entity.getSubject());
            notificiationDTO.setRetryCount(entity.getRetryCount());
            notificiationDTO.setChannelType(entity.getChannelType());
            notificiationDTO.setPriority(entity.getPriority());
            notificiationDTO.setStatus(entity.getStatus());
            notificiationDTO.setPayload(entity.getPayload());
            notificiationDTO.setMailBody(entity.getMailBody());
            notificiationDTO.setFailureReason(entity.getFailureReason());
            notificiationDTO.setScheduledAt(entity.getScheduledAt());
            notificiationDTO.setCreatedAt(entity.getCreatedAt());
            notificiationDTO.setUpdatedAt(entity.getUpdatedAt());
            return notificiationDTO;
        }

        public static List<NotificiationDTO> mapNotificationEntityListToDTOList(List<NotificationsEntity> entities){
            return entities.stream().map(NotificationsMapper::mapNotificationEntityToDTO).toList();
        }
}
