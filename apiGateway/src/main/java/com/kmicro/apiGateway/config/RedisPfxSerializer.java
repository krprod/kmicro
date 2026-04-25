package com.kmicro.apiGateway.config;

import com.kmicro.apiGateway.constants.AppContants;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RedisPfxSerializer implements RedisSerializer<String> {

    private final String prefix;
    private final RedisSerializer<String> stringSerializer = RedisSerializer.string();

    public RedisPfxSerializer(String serviceName) {
//        this.prefix = serviceInitials(serviceName) + ":";
        this.prefix = AppContants.SERVICE_REDIS_KEY_PREFIX + ":";
    }

    @Override
    public byte[] serialize(String string) {
        // Prepend prefix before saving to Redis
        return stringSerializer.serialize(string == null ? null : prefix + string);
    }

    @Override
    public String deserialize(byte[] bytes) {
        // Strip prefix when reading back into Java
        String key = stringSerializer.deserialize(bytes);
        return (key != null && key.startsWith(prefix))
                ? key.substring(prefix.length())
                : key;
    }

    private String serviceInitials(String serviceName){
        return Arrays.stream(serviceName.split("-"))
                .filter(word -> !word.isEmpty()) // Handle double hyphens like "user--service"
                .map(word -> String.valueOf(word.charAt(0)))
                .collect(Collectors.joining())
                .toUpperCase();
    }

}//EC
