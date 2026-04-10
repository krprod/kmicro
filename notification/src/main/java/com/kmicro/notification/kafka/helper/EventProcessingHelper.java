package com.kmicro.notification.kafka.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kmicro.notification.constansts.KafkaConstants;
import com.kmicro.notification.entities.NotificationsEntity;
import com.kmicro.notification.entities.UserAddressEntity;
import com.kmicro.notification.entities.UserDataEntity;
import com.kmicro.notification.mapper.NotificationsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class EventProcessingHelper {

    public NotificationsEntity getNewNotificationEntity(String sendto, String subject, String fragName, Map<String, Object> mailBodyMap, Map<String, Object> payload){
        NotificationsEntity notification = new NotificationsEntity();
        notification.setSendTo(sendto);
        notification.setSubject(subject);
        notification.setFragment(fragName);
        notification.setMailBody(mailBodyMap);
        notification.setPayload(payload);
        NotificationsMapper.addFixedFields(notification);
        return  notification;
    }

    public UserDataEntity initialUserEntityFromJson(JsonNode userDataJson){
        UserDataEntity user = new UserDataEntity();

        if(userDataJson.get(KafkaConstants.DT_USER_ID) != null){
            user.setUserId(userDataJson.get(KafkaConstants.DT_USER_ID).asInt());
        }
        if(userDataJson.get(KafkaConstants.DT_EMAIL) != null) {
            user.setEmail(userDataJson.get(KafkaConstants.DT_EMAIL).asText());
        }
        if(userDataJson.get(KafkaConstants.DT_LOGIN_NAME) != null){
            user.setLoginName(userDataJson.get(KafkaConstants.DT_LOGIN_NAME).asText());
        }
        if(null != userDataJson.get(KafkaConstants.DT_UNAME)){
            user.setUserName(userDataJson.get(KafkaConstants.DT_UNAME).asText());
        }
        if(null != userDataJson.get(KafkaConstants.DT_CONTACT)){
            user.setContact(userDataJson.get(KafkaConstants.DT_CONTACT).asText());
        }

        return user;
    }

    public UserDataEntity updateUserData(UserDataEntity userDataEntity, JsonNode userDataJson){
        if(!StringUtils.hasText(userDataEntity.getUserName()) && null != userDataJson.get(KafkaConstants.DT_UNAME)){
            userDataEntity.setUserName(userDataJson.get(KafkaConstants.DT_UNAME).asText());
        }
        if(!StringUtils.hasText(userDataEntity.getContact()) && null != userDataJson.get(KafkaConstants.DT_CONTACT)){
            userDataEntity.setContact(userDataJson.get(KafkaConstants.DT_CONTACT).asText());
        }
        return userDataEntity;
    }

    public UserAddressEntity updateAddressEntity(UserAddressEntity addressEntity, JsonNode addressJson){

        if(null == addressEntity){
            UserAddressEntity newAddress = new UserAddressEntity();
            if(null != addressJson.get(KafkaConstants.DT_ADDRS_ID)) newAddress.setAddressId(addressJson.get(KafkaConstants.DT_ADDRS_ID).asInt());
            if(null != addressJson.get(KafkaConstants.DT_USER_ID)) newAddress.setUserId(addressJson.get(KafkaConstants.DT_USER_ID).asInt());
            if(null != addressJson.get(KafkaConstants.DT_CITY)) newAddress.setCity(addressJson.get(KafkaConstants.DT_CITY).asText());
            if(null != addressJson.get(KafkaConstants.DT_COUNTRY)) newAddress.setCountry(addressJson.get(KafkaConstants.DT_COUNTRY).asText());
            if(null != addressJson.get(KafkaConstants.DT_SHIP_ADDRS)) newAddress.setShipping_address(addressJson.get(KafkaConstants.DT_SHIP_ADDRS).asText());
            if(null != addressJson.get(KafkaConstants.DT_ZCODE)) newAddress.setZipCode(addressJson.get(KafkaConstants.DT_ZCODE).asText());
            if(null != addressJson.get(KafkaConstants.DT_STATE)) newAddress.setState(addressJson.get(KafkaConstants.DT_STATE).asText());
            return  newAddress;
        }

      /*  if(null != addressEntity.getAddressId() && null != addressEntity.getUserId()){
            addressEntity.setAddressId(addressJson.get(KafkaConstants.DT_ADDRS_ID).asInt());
            addressEntity.setUserId(addressJson.get(KafkaConstants.DT_USER_ID).asInt());
        }*/
       /* if(!StringUtils.hasText(addressEntity.getCity())){
            addressEntity.setCity(addressJson.get(KafkaConstants.DT_CITY).asText());
        }

        if(!StringUtils.hasText(addressEntity.getCountry())){
            addressEntity.setCountry(addressJson.get(KafkaConstants.DT_COUNTRY).asText());
        }

        if(!StringUtils.hasText(addressEntity.getShipping_address())){
            addressEntity.setShipping_address(addressJson.get(KafkaConstants.DT_SHIP_ADDRS).asText());
        }
        if(!StringUtils.hasText(addressEntity.getZipCode())){
            addressEntity.setZipCode(addressJson.get(KafkaConstants.DT_ZCODE).asText());
        }
        if(!StringUtils.hasText(addressEntity.getState())){
            addressEntity.setState(addressJson.get(KafkaConstants.DT_STATE).asText());
        }*/

        //---- UPDATING RECENT DATA FOR USER ADDRESS, CAME FROM USER SERVICE
        /*if(null != addressJson.get(KafkaConstants.DT_ADDRS_ID)) addressEntity.setAddressId(addressJson.get(KafkaConstants.DT_ADDRS_ID).asInt());
        if(null != addressJson.get(KafkaConstants.DT_USER_ID)) addressEntity.setUserId(addressJson.get(KafkaConstants.DT_USER_ID).asInt());*/
        if(null != addressJson.get(KafkaConstants.DT_CITY)) addressEntity.setCity(addressJson.get(KafkaConstants.DT_CITY).asText());
        if(null != addressJson.get(KafkaConstants.DT_COUNTRY)) addressEntity.setCountry(addressJson.get(KafkaConstants.DT_COUNTRY).asText());
        if(null != addressJson.get(KafkaConstants.DT_SHIP_ADDRS)) addressEntity.setShipping_address(addressJson.get(KafkaConstants.DT_SHIP_ADDRS).asText());
        if(null != addressJson.get(KafkaConstants.DT_ZCODE)) addressEntity.setZipCode(addressJson.get(KafkaConstants.DT_ZCODE).asText());
        if(null != addressJson.get(KafkaConstants.DT_STATE)) addressEntity.setState(addressJson.get(KafkaConstants.DT_STATE).asText());
        return addressEntity;
    }

    public UserDataEntity createUserDataEntity(JsonNode userDetailJSON) {
        return UserDataEntity.builder()
//                .userId(userDetailJSON.get("user_id").asInt())
//                .addressId(userDetailJSON.get("address_id").asInt())
//                .state(userDetailJSON.get("state").asText())
//                .country(userDetailJSON.get("country").asText())
//                .city(userDetailJSON.get("city").asText())
//                .zipCode(userDetailJSON.get("zip_code").asText())
//                .shipping_address(userDetailJSON.get("shipping_address").asText())
//                .recipientName(userDetailJSON.get("name").asText())
//                .email(userDetailJSON.get("email").asText())
//                .contact(userDetailJSON.get("contact").asText())
                .build();
    }

    public Map<String, Object> getUserDataMap(UserDataEntity userData) {
//            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> userDataMap = new HashMap<>();

            userDataMap.put(KafkaConstants.DT_UNAME, userData.getUserName());
            userDataMap.put(KafkaConstants.DT_CONTACT, userData.getContact());
            userDataMap.put(KafkaConstants.DT_EMAIL, userData.getEmail());

//        dataMap.put(KafkaConstants.DT_USER_DATA, userDataMap);
            return userDataMap;
//            mailBody.put("details", details);
    }

    public ObjectNode getUserDataJson(UserDataEntity userData, ObjectNode data) {
//            Map<String, Object> dataMap = new HashMap<>();
//        Map<String, Object> userDataMap = new HashMap<>();

        data.put(KafkaConstants.DT_UNAME, userData.getUserName());
        data.put(KafkaConstants.DT_CONTACT, userData.getContact());
        data.put(KafkaConstants.DT_EMAIL, userData.getEmail());

//        dataMap.put(KafkaConstants.DT_USER_DATA, userDataMap);
//        return userDataMap;
        return data;
//            mailBody.put("details", details);
    }

    public Map<String, Object> getAddressDataMap(UserAddressEntity addressEntity){
//        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> addressMap = new HashMap<>();

        addressMap.put(KafkaConstants.DT_COUNTRY, addressEntity.getCountry());
        addressMap.put(KafkaConstants.DT_CITY, addressEntity.getCity());
        addressMap.put(KafkaConstants.DT_ADDRS_ID, addressEntity.getAddressId());
        addressMap.put(KafkaConstants.DT_SHIP_ADDRS, addressEntity.getShipping_address());
        addressMap.put(KafkaConstants.DT_ZCODE, addressEntity.getZipCode());

//        dataMap.put(KafkaConstants.DT_ADDRESS_NODE, addressMap);
        return addressMap;

    }

    public ObjectNode getAddressDataJson(UserAddressEntity addressEntity, ObjectNode data){
//        Map<String, Object> dataMap = new HashMap<>();
//        Map<String, Object> addressMap = new HashMap<>();

        data.put(KafkaConstants.DT_COUNTRY, addressEntity.getCountry());
        data.put(KafkaConstants.DT_CITY, addressEntity.getCity());
        data.put(KafkaConstants.DT_ADDRS_ID, addressEntity.getAddressId());
        data.put(KafkaConstants.DT_SHIP_ADDRS, addressEntity.getShipping_address());
        data.put(KafkaConstants.DT_ZCODE, addressEntity.getZipCode());

//        dataMap.put(KafkaConstants.DT_ADDRESS_NODE, addressMap);
        return data;
    }

    public ObjectNode getRequestUserDataObj(int userID, int addressID, String id, ObjectNode requestData) {
        requestData.put("user_id",userID);
        requestData.put("address_id", addressID);
        requestData.put("notification_id",id);
        return requestData;
    }
}//EC
