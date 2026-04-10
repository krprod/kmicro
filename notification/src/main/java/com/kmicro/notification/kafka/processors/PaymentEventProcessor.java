package com.kmicro.notification.kafka.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kmicro.notification.constansts.KafkaConstants;
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
public class PaymentEventProcessor {
    private final DBOps DBOps;
    private final EmailUtils emailUtils;
    private final EventProcessingHelper eventProcessingHelper;
    private final CommonHelperUtils commonHelperUtils;
    private final ObjectMapper objectMapper;
    private final ExternalEventProducers externalEventProducers;

    private final Set<String> SetOfEventTypes = Set.of(
            KafkaConstants.ET_PAYMENT_STATUS_UPDATE
    );

    public <T> void processRawEvent(T data, String eventType){
        if(!SetOfEventTypes.contains(eventType)) return; // Only Accept Few Event types

        try {
            JsonNode payloadJson = commonHelperUtils.getJsonNodeFromString(data.toString());
            ObjectNode mailBodyJson = (ObjectNode) payloadJson.get(KafkaConstants.DT_BODY);
            String paymentStatus = mailBodyJson.get("payment").get("payment_status").asText();

                switch (paymentStatus){
                    case "PAYMENT_SUCCESS" -> this.paymentSuccessNTF(payloadJson,eventType, mailBodyJson, data);
                    case "PAYMENT_FAILED" -> this.paymentFailNTF(payloadJson,eventType, mailBodyJson, data);
                }

//            this.paymentFailNTF(payloadJson,eventType, mailBodyJson, data);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void paymentSuccessNTF(JsonNode payloadJson, String eventType, ObjectNode mailBodyJson, T data) {
        String mailSubject =  payloadJson.get(KafkaConstants.DT_SUBJECT).asText();
        String sendTo =  payloadJson.get(KafkaConstants.DT_SEND_TO).asText();

        NotificationsEntity notification = new NotificationsEntity();
        notification.setSendTo(sendTo);
        notification.setFragment(emailUtils.getFragment(Templates.FR_PAYMENT_SUCCESS.name()));
        notification.setSubject(mailSubject);
        NotificationsMapper.addFixedFields(notification);

        notification.setMailBody(commonHelperUtils.getDataMapFromContent(mailBodyJson));
        notification.setPayload(commonHelperUtils.getDataMapFromContent(data));

        NotificationsEntity savedNotification = DBOps.saveDataInDB(notification);

     /*   boolean requestUserService = false;
        int userID = 0;
        int addressID = 0;
        ObjectNode dataMap = objectMapper.createObjectNode();

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
        }*/
    }

    private <T> void paymentFailNTF(JsonNode payloadJson, String eventType, ObjectNode mailBodyJson, T data) {
        String mailSubject =  payloadJson.get(KafkaConstants.DT_SUBJECT).asText();
        String sendTo =  payloadJson.get(KafkaConstants.DT_SEND_TO).asText();

        NotificationsEntity notification = new NotificationsEntity();
        notification.setSendTo(sendTo);
        notification.setFragment(emailUtils.getFragment(Templates.FR_PAYMENT_FAIL.name()));
        notification.setSubject(mailSubject);
        NotificationsMapper.addFixedFields(notification);

        notification.setMailBody(commonHelperUtils.getDataMapFromContent(mailBodyJson));
        notification.setPayload(commonHelperUtils.getDataMapFromContent(data));

        NotificationsEntity savedNotification = DBOps.saveDataInDB(notification);
    }

    private String getFragName(String eventType){
        return switch (eventType){
            case KafkaConstants.ET_ORDER_CONFIRMERD -> emailUtils.getFragment(Templates.FR_ORDER_CONFIRM.name());
            case KafkaConstants.ET_PAYMENT_STATUS_UPDATE -> emailUtils.getFragment(Templates.FR_ORDER_CONFIRM.name());
            case KafkaConstants.ET_ORDER_STATUS_UPDATED -> emailUtils.getFragment(Templates.FR_SHIPPING_UPDATE.name());
            default -> "";
        };
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
