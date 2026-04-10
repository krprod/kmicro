package com.kmicro.user.config;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kmicro.user.constants.AppContants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class RedisCacheConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // 1. Create a Jackson Serializer for values
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Handles LocalDateTime
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);


        // 1.2. EXPLICITLY configure Polymorphic Typing
        // This adds the "@class" property to the JSON so Jackson knows how to rebuild the List
   /*     BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);*/

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(mapper);

        // 2. Build the configuration
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                // Use your custom prefix logic
                .computePrefixWith(cacheName -> AppContants.SERVICE_REDIS_KEY_PREFIX + ":" + cacheName + ":")
                // Tell the Cache Layer to use JSON, not DefaultSerializer
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//        return RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(cacheConfiguration())
//                .build();

            Map<String, RedisCacheConfiguration> cacheConfigurationDiff = new HashMap<>();

            // User cache: 24 hours (Long lived)
            cacheConfigurationDiff.put(AppContants.CACHE_USER_KEY_PX, cacheConfiguration().entryTtl(Duration.ofMinutes(20)));
            cacheConfigurationDiff.put(AppContants.CACHE_ADDRESS_KEY_PX, cacheConfiguration().entryTtl(Duration.ofMinutes(30)));

            // Auth/Login tokens: 15 minutes (Short lived)
            //cacheConfigurationDiff.put("login_tokens", cacheConfiguration().entryTtl(Duration.ofMinutes(15)));

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration())
                .withInitialCacheConfigurations(cacheConfigurationDiff)
                .build();
        return new TransactionAwareCacheManagerProxy(redisCacheManager);
    }

    /*@Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("order:cache:") // Dedicated sub-prefix for cache
                .entryTtl(Duration.ofMinutes(10))    // Auto-delete after 10 mins
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }*/

}//EC
