package com.kmicro.payment.controller;

//import com.kmicro.payment.config.KafkaProducer;
import com.kmicro.payment.dtos.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    private final KafkaProducer kafkaJsonProducer;

    public KafkaController(KafkaProducer kafkaJsonProducer) {
        this.kafkaJsonProducer = kafkaJsonProducer;
    }

    @PostMapping("/send-user")
    public String sendUser(@RequestBody User user) {
        kafkaJsonProducer.sendUser(user);
        return "User Sent!";
    }
}
*/
