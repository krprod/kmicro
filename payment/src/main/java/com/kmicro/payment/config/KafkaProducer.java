package com.kmicro.payment.config;

import com.kmicro.payment.dtos.OrderResponse;
import com.kmicro.payment.dtos.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
/*

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, User> kafkaTemplate;
    private static final String TOPIC = "user-topic";

    public KafkaProducer(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUser(User user) {
        String key = user.getName()+"_"+TOPIC;
        kafkaTemplate.send(TOPIC,key, user);
        System.out.println("Produced User: " + user);
    }
}
*/
/*
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "my-topic";

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
        System.out.println("Produced message: " + message);
    }
}*/

