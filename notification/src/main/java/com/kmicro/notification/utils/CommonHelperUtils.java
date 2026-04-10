package com.kmicro.notification.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class CommonHelperUtils {

    private final ObjectMapper objectMapper;

    public CommonHelperUtils(){
            this.objectMapper = new ObjectMapper();
    }

    public void chaosMonkey(Boolean enabled){
        if(!enabled) return;
        try {
            Thread.sleep(5000);
            double  failureProbability = 0.5;
            double chance = ThreadLocalRandom.current().nextDouble();
            if (chance < failureProbability) {
                log.error("❌ RANDOM ERROR: Chaos Monkey triggered! (Chance: {} < Probability: {})",
                        String.format("%.2f", chance), failureProbability);

                throw  new RuntimeException(new MessagingException("Meri Marzi"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //            log.info("✅ SUCCESS: Operation completed. (Chance: {} >= Probability: {})",
//                    String.format("%.2f", chance), failureProbability);
//            String Text = getTextFromMessage(mailMessage);
//           log.info("Text: {}",Text);
    }

    public Map<String, Object> getMapObjectFromJsonNode(JsonNode data){
        return objectMapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
    }

    public <T> Map<String, Object> createMapObject(T data){
        return objectMapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
    }

    public  Map<String, Object> getMapObjectFromString(String data){
        try {
            JsonNode node = this.getJsonNodeFromString(data);
            return getMapObjectFromJsonNode(node);
        }catch (JacksonException e) {
            log.error("Casting String Msg to JsonNode Failed", e);
            throw new RuntimeException(e);
        }
    }

    public  <T> Map<String, Object> getDataMapFromContent(T data){
        Map<String, Object> dataMap = new HashMap<>();
        if(data instanceof String d1){
            dataMap = this.getMapObjectFromString(d1);
        } else if (data instanceof JsonNode d2) {
            dataMap = this.getMapObjectFromJsonNode(d2);
        }else{
            dataMap = this.createMapObject(data);
        }
        log.info("DataMapFromContent Generated Successfully");
        return dataMap;
    }

    public JsonNode getJsonNodeFromString(String data) throws JsonProcessingException {
        return objectMapper.readTree(data);
    }

}
