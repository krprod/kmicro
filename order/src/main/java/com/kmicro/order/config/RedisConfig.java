package com.kmicro.order.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kmicro.order.dtos.CartDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return  new LettuceConnectionFactory();
    }


    @Bean
    @Primary
    public RedisTemplate<String , Object> redisTemplateObject(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = createObjectMapper();

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(mapper,Object.class);
        //------ Custom Prefixing Serializer for Keys
        RedisPfxSerializer prefixSerializer = new RedisPfxSerializer(serviceName);
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(prefixSerializer);
        template.setValueSerializer(serializer);
        return template;
    }

    @Qualifier("redisTemplateCart")
    @Bean
    public RedisTemplate<String, CartDTO> redisTemplateCart(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CartDTO> template = new RedisTemplate<>();

        ObjectMapper mapper = createObjectMapper();

        // Pass ObjectMapper directly to the constructor
        Jackson2JsonRedisSerializer<CartDTO> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(mapper, CartDTO.class);
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        //------ Custom Prefixing Serializer for Keys
        RedisPfxSerializer prefixSerializer = new RedisPfxSerializer(serviceName);

        template.setConnectionFactory(connectionFactory);

        // Key Serializer -- Hash Key Serializer
        template.setKeySerializer(prefixSerializer);
        template.setHashKeySerializer(prefixSerializer);

        // Hash Value Serializer (to support complex objects) -- Value Serializer
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Qualifier("redisTemplateClassCast")
    @Bean
    public RedisTemplate<String, Object> redisTemplateClassCast(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = createObjectMapper();
        // This is the "Magic" line: It enables polymorphic type handling
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);
        //------ Custom Prefixing Serializer for Keys
        RedisPfxSerializer prefixSerializer = new RedisPfxSerializer(serviceName);

//        template.setKeySerializer(new StringRedisSerializer());
        template.setKeySerializer(prefixSerializer);
        template.setValueSerializer(serializer);
        return template;
    }

    private ObjectMapper createObjectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                // Senior Security Tip: Enable default typing for polymorphism if needed
                // .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL)
                .build();
    }
}//EC
