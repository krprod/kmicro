package com.kmicro.order.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaProducerConfig {

    private KafkaProducer<String, String> kafkaProducerStringKey = null;

//    @Value("${kafka.bootstrap-servers}")
//    private String BOOTSTRAP_SERVERS_CONFIG;

    @Bean(name = "KafkaProducerStringKey")
    public Producer<String, String> createKafkaProducer() {
        final Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        properties.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConstants.CLIENT_ID_CONFIG);
        properties.put(ProducerConfig.RETRIES_CONFIG, 2);
        kafkaProducerStringKey = new KafkaProducer<String, String>(properties);
        return kafkaProducerStringKey;
    }
}
