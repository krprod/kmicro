package com.kmicro.apiGateway.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kmicro.apiGateway.constants.AppContants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class ReactiveRedisConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        if (redisPassword != null && !redisPassword.isBlank()) {
            config.setPassword(redisPassword);
        }
        return new LettuceConnectionFactory(config);
    }

    /**
     * Standard Reactive Template for General Objects
     */
    @Bean
    @Primary
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        ObjectMapper mapper = createBaseObjectMapper();

        // Serializers
        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(mapper, Object.class);
        RedisPfxSerializer keySerializer = new RedisPfxSerializer(AppContants.SERVICE_REDIS_KEY_PREFIX);

        RedisSerializationContext<String, Object> context = RedisSerializationContext
                .<String, Object>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    /**
     * Specialized Template for Polymorphic Type Handling (Class Casting)
     */
    @Bean
    @Qualifier("reactiveRedisTemplateClassCast")
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplateClassCast(ReactiveRedisConnectionFactory factory) {
        ObjectMapper mapper = createBaseObjectMapper();
        // Enable polymorphic typing
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(mapper);
        RedisPfxSerializer keySerializer = new RedisPfxSerializer(serviceName);

        RedisSerializationContext<String, Object> context = RedisSerializationContext
                .<String, Object>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    private ObjectMapper createBaseObjectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }
}