package com.kmicro.user.kafka.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.dtos.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Component
public class MapCreationHelper {
    private final ObjectMapper objectMapper;

    MapCreationHelper(ObjectMapper objectMapper){
         this.objectMapper = objectMapper;
    }

    public String createEmailVerifcationMap(UserDTO user, String msgLine, String verifyLink){
        try {
            Map<String, Object> mailBodyMap = new HashMap<>();
            Map<String, Object> mailMap = new HashMap<>();
            Map<String, Object> userDetails = new HashMap<>();

            addUserMap(user,userDetails);

            mailBodyMap.put("title", "Verify Your Email");
            mailBodyMap.put("greetingByName", "Hi "+user.getLogin_name());
            mailBodyMap.put("msgLine1", "Click on the below link button to verify your email address");
            mailBodyMap.put("verifyLink", verifyLink);

            mailMap.put("sendto",user.getEmail());
            mailMap.put("subject", "Email verification");
            mailMap.put("body",mailBodyMap);
            mailMap.put("userData",userDetails);
            return objectMapper.writeValueAsString(mailMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String createWelcomEmailMap(UserDTO user, String msgLine, String couponCode){
        try {
            String coupon  = null != couponCode && !couponCode.isEmpty() ? couponCode : "WELCOME100";
            Map<String, Object> mailBodyMap = new HashMap<>();
            Map<String, Object> mailMap = new HashMap<>();
            Map<String, Object> userDetails = new HashMap<>();

            addUserMap(user,userDetails);

            mailBodyMap.put("title", "Welcome To Kmicro");
            mailBodyMap.put("userName", "Hi "+ StringUtils.capitalize(user.getFirstName())+" "+ StringUtils.capitalize(user.getLastName()));
            mailBodyMap.put("msgLine1", "We're thrilled to have you here. To get you started, use the code below for 10% off your first order.");
            mailBodyMap.put("discountCode", coupon);

            mailMap.put("sendto",user.getEmail());
            mailMap.put("subject", "Welcome To Kmicro");
            mailMap.put("body",mailBodyMap);
            mailMap.put("userData",userDetails);
            return objectMapper.writeValueAsString(mailMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String createPasswordResetEmailMap(UserDTO user, String msgLine, String verifyLink){
        try {
            Map<String, Object> mailBodyMap = new HashMap<>();
            Map<String, Object> mailMap = new HashMap<>();
            Map<String, Object> userDetails = new HashMap<>();

            addUserMap(user,userDetails);

            mailBodyMap.put("title", "Verify Your Email");
            mailBodyMap.put("greetingByName", "Hi "+user.getLogin_name());
            mailBodyMap.put("msgLine1", "Click on the below link button to verify your email address");
            mailBodyMap.put("verifyLink", verifyLink);

            mailMap.put("sendto",user.getEmail());
            mailMap.put("subject", "Email verification");
            mailMap.put("body",mailBodyMap);
            mailMap.put("userData",userDetails);
            return objectMapper.writeValueAsString(mailMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAddressMap(AddressDTO address, Map<String, Object> userDetails){
        if(address != null){
            userDetails.put("address_id",address.getId());
            userDetails.put("user_id",address.getUserId());
            userDetails.put("country",address.getCountry());
            userDetails.put("state", address.getState());
            userDetails.put("city",address.getCity());
            userDetails.put("shipping_address",address.getAddressLine1()+" "+ address.getAddressLine2());
            userDetails.put("zip_code",address.getZipCode());
        }else {
            userDetails.put("address_id",0);
        }
    }

    public void addUserMap(UserDTO userDTO, Map<String, Object> userDetails){
//        Stream.of(
//                entry("name",userDTO.getFirstName()),
//                entry("contact", userDTO.getContactNumber()),
//                entry("email",userDTO.getEmail()),
//                entry("user_id", userDTO.getId()),
//                entry("login_name", userDTO.getLogin_name())
//
//        ).forEach(e->{
//            if(StringUtils.hasText(e.getValue())) userDetails.put(e.getKey(), e.getValue());
//        });
        if(StringUtils.hasText(userDTO.getFirstName()) && StringUtils.hasText(userDTO.getContactNumber()))  {
            userDetails.put("name",userDTO.getFirstName());
            userDetails.put("contact",userDTO.getContactNumber());
        }
        if(StringUtils.hasText(userDTO.getEmail()) && StringUtils.hasText(userDTO.getLogin_name()))  {
            userDetails.put("email",userDTO.getEmail());
            userDetails.put("user_id", userDTO.getId());
            userDetails.put("login_name", userDTO.getLogin_name());
        }
    }
}
