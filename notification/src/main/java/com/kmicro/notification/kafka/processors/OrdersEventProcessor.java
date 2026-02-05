package com.kmicro.notification.kafka.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kmicro.notification.constansts.KafkaConstants;
import com.kmicro.notification.constansts.Status;
import com.kmicro.notification.constansts.Templates;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.entities.UserAddressEntity;
import com.kmicro.notification.entities.UserDataEntity;
import com.kmicro.notification.kafka.helper.EventProcessingHelper;
import com.kmicro.notification.kafka.producers.ExternalEventProducers;
import com.kmicro.notification.mapper.NotificationsMapper;
import com.kmicro.notification.utils.CommonHelperUtils;
import com.kmicro.notification.utils.DBOps;
import com.kmicro.notification.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersEventProcessor {

    private final DBOps DBOps;
    private final EmailUtils emailUtils;
    private final EventProcessingHelper eventProcessingHelper;
    private final CommonHelperUtils commonHelperUtils;
    private final ObjectMapper objectMapper;
    private final ExternalEventProducers externalEventProducers;

    private final Set<String> SetOfEventTypes = Set.of(
            KafkaConstants.ET_ORDER_CONFIRMERD,
            KafkaConstants.ET_ORDER_CREATED,
            KafkaConstants.ET_ORDER_STATUS_UPDATED
    );

    public <T> void processRawEvent(T data, String eventType){
        if(!SetOfEventTypes.contains(eventType)) return; // Only Accept Few Event types

        try {

            switch (eventType){
                case KafkaConstants.ET_ORDER_CONFIRMERD -> this.ordersNotification(data, eventType);
//                case KafkaConstants.ET_ORDER_CREATED -> this.orderConfirmationNtf(data, eventType);
                case KafkaConstants.ET_ORDER_STATUS_UPDATED -> this.ordersNotification(data, eventType);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void ordersNotification(T data, String eventType){
       try {
//           Map<String, Object> payload = commonHelperUtils.getDataMapFromContent(data);
//           Map<String, Object> mailBodyMap = commonHelperUtils.getDataMapFromContent(payload.get(KafkaConstants.DT_BODY));
//           JsonNode mailBodyJson = objectMapper.convertValue(mailBodyMap, JsonNode.class);
           JsonNode payloadJson = commonHelperUtils.getJsonNodeFromString(data.toString());
           ObjectNode mailBodyJson = (ObjectNode) payloadJson.get(KafkaConstants.DT_BODY);
           ObjectNode dataMap = objectMapper.createObjectNode();
           boolean requestUserService = false;
           int userID = 0;
           int addressID = 0;

           /*String mailSubject =  payload.get(KafkaConstants.DT_SUBJECT).toString();
           String sendTo =  payload.get(KafkaConstants.DT_SEND_TO).toString();*/

           String mailSubject =  payloadJson.get(KafkaConstants.DT_SUBJECT).asText();
           String sendTo =  payloadJson.get(KafkaConstants.DT_SEND_TO).asText();

           NotificationsEntity notification = new NotificationsEntity();
           notification.setSendTo(sendTo);
           notification.setFragment(getFragName(eventType));
           notification.setSubject(mailSubject);
           NotificationsMapper.addFixedFields(notification);

           if(sendTo.startsWith("TEMPA-")){
               userID = Integer.parseInt(sendTo.substring(6));
               //---- ASSUMING ADDRESS ID CANNOT BE 0, ALWAYS PICK EXISTING USER SERVICE ADDRESS---
               addressID = mailBodyJson.get("details").get("address_id").asInt();

               dataMap = this.getUserAndAddressObjNode(userID, addressID, dataMap);

               if(null == dataMap.get(KafkaConstants.DT_USER_DATA) && null == dataMap.get(KafkaConstants.DT_ADDRESS_NODE)){
                   notification.setStatus(Status.REQUEST_USER_SERVICE);
                   notification.setRecipientId(userID);
                   requestUserService = true;
               } else {
//                   mailBodyMap.put("details",detailsMap);
                   mailBodyJson.set("details",dataMap);
               }
           }

           notification.setMailBody(commonHelperUtils.getDataMapFromContent(mailBodyJson));
           notification.setPayload(commonHelperUtils.getDataMapFromContent(data));

           NotificationsEntity savedNotification = DBOps.saveDataInDB(notification);

           if(requestUserService){
               ObjectNode requestData = objectMapper.createObjectNode();
               requestData = eventProcessingHelper.getRequestUserDataObj(userID, addressID, savedNotification.getId().toString(), requestData);
               externalEventProducers.requestUserData(savedNotification.getId().toString(), requestData);
           }

       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }

    private String getFragName(String eventType){
        return switch (eventType){
                    case KafkaConstants.ET_ORDER_CONFIRMERD -> emailUtils.getFragment(Templates.FR_ORDER_CONFIRM.name());
//                    case KafkaConstants.ET_ORDER_CREATED -> emailUtils.getFragment(Templates.FR_ORDER_CONFIRM.name());
                    case KafkaConstants.ET_ORDER_STATUS_UPDATED -> emailUtils.getFragment(Templates.FR_SHIPPING_UPDATE.name());
                    default -> "";
        };
    }

    private void flushEntity(NotificationsEntity entity){
        DBOps.saveDataInDB(entity);
        log.info("Entity Flushed Successful");
    }

    private Map<String, Object> checkUserAndAddress(Integer userID, Integer addressID){
        Optional<UserDataEntity> userOpt = DBOps.getUserFromDB(userID);
        Map<String, Object> detailParentMap = new HashMap<>();

        /*Optional<UserAddressEntity> addressOpt = addressID > 0
                ?  DBOps.getAddressByUserId(addressID, userID)
                : Optional.empty();*/

        if(userOpt.isPresent()){

            detailParentMap.put(KafkaConstants.DT_USER_DATA, eventProcessingHelper.getUserDataMap(userOpt.get())) ;
            log.info("userData Found In DB");

            Optional<UserAddressEntity> addressOpt =   DBOps.getAddressByUserId(addressID, userID);
            if(addressOpt.isPresent()){
                log.info("address Found In DB");
                detailParentMap.put(KafkaConstants.DT_ADDRESS_NODE,
                        eventProcessingHelper.getAddressDataMap(addressOpt.get())
                ) ;
            }
        }
//        else {
//            detailParentMap.put(KafkaConstants.DT_USER_DATA,"");
//        }

//        else {
//            detailParentMap.put(KafkaConstants.DT_ADDRESS_NODE, "");
//        }
       return detailParentMap;
    }

    private  ObjectNode getUserAndAddressObjNode(Integer userID, Integer addressID, ObjectNode dataMap){
        ObjectNode userData = objectMapper.createObjectNode();
        ObjectNode addressData = objectMapper.createObjectNode();
//        ObjectNode dataMap = objectMapper.createObjectNode();

        Optional<UserDataEntity> userOpt = DBOps.getUserFromDB(userID);
        if(userOpt.isPresent()){
            dataMap.set(KafkaConstants.DT_USER_DATA, eventProcessingHelper.getUserDataJson(userOpt.get(), userData)) ;
            log.info("userData Found In DB");
            Optional<UserAddressEntity> addressOpt =   DBOps.getAddressByUserId(addressID, userID);
            if(addressOpt.isPresent()){
                log.info("address Found In DB");
                dataMap.set(KafkaConstants.DT_ADDRESS_NODE, eventProcessingHelper.getAddressDataJson(addressOpt.get(), addressData)) ;
            }
        }
        return dataMap;
    }



}//EC
