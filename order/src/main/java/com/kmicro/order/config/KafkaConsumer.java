package com.kmicro.order.config;

import com.kmicro.order.dtos.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/*
@Service
public class KafkaConsumer {

    @KafkaListener(topics = "user-topic", groupId = "my-group-order", containerFactory = "userKafkaListenerFactory")
    public void consumeUser(User user) {
        System.out.println("Consumed User In OrderService: " + user);
    }
}*/
