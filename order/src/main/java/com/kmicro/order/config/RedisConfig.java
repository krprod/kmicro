package com.kmicro.order.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return  new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String , Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(mapper,Object.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        return template;
    }

  /*  @Bean
    public RedisTemplate<String, CartDTO> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CartDTO> template = new RedisTemplate<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Pass ObjectMapper directly to the constructor
        Jackson2JsonRedisSerializer<CartDTO> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, CartDTO.class);

        template.setConnectionFactory(connectionFactory);
        // Key Serializer
        template.setKeySerializer(new StringRedisSerializer());
        // Hash Key Serializer
        template.setHashKeySerializer(new StringRedisSerializer());
        // Hash Value Serializer (to support complex objects)
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        // Value Serializer
        template.setValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }*/



/*    @Bean()
    public RedisTemplate<String, CartDTO> redisTemplate1(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CartDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key Serializer
//        template.setKeySerializer(new StringRedisSerializer());

        // Hash Key Serializer
        template.setHashKeySerializer(new StringRedisSerializer());

        // Hash Value Serializer (to support complex objects)
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(CartDTO.class));

        // Value Serializer
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }*/
}//EC
