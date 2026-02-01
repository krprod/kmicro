package com.kmicro.user.config;

import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RedisPfxSerializer extends StringRedisSerializer {

    private final String prefix;
    private final StringRedisSerializer stringSerializer = new StringRedisSerializer();

    public RedisPfxSerializer(String serviceName) {
        this.prefix = serviceInitials(serviceName) + ":";
    }

    @Override
    public byte[] serialize(String string) {
        // Prepend prefix before saving to Redis
        return super.serialize(string == null ? null : prefix + string);
    }

    @Override
    public String deserialize(byte[] bytes) {
        // Strip prefix when reading back into Java
        String key = super.deserialize(bytes);
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
