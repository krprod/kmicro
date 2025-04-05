package com.kmicro.payment.config;

import com.kmicro.payment.dtos.OrderResponse;
import com.kmicro.payment.dtos.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
/*

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "user-topic", groupId = "my-group", containerFactory = "userKafkaListenerFactory")
    public void consumeUser(User user) {
        System.out.println("Consumed User: " + user);
    }
}
//public class KafkaConsumer {
//
//    @KafkaListener(topics = "my-topic", groupId = "my-group")
//    public void consume(String message) {
//        System.out.println("Consumed message: " + message);
//    }
//}
*/
