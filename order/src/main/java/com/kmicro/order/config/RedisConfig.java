package com.kmicro.order.config;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
/*
@Configuration
public class RedisConfig {

            @Bean
            public RedisConnectionFactory redisConnectionFactory() {
                return  new LettuceConnectionFactory();
            }

            *//*
                @Bean
                public RedisTemplate<String , Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                    RedisTemplate<String, Object> template = new RedisTemplate<>();
                    template.setConnectionFactory(connectionFactory);
                    template.setKeySerializer(new StringRedisSerializer());
                    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
                    return template;
                }
            *//*

            @Bean
            public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                // Pass ObjectMapper directly to the constructor
                Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

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
            }



            *//*    @Bean()
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
                }*//*
}*/
