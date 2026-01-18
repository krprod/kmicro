package com.kmicro.user.config;

import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisPfxSerializer extends StringRedisSerializer {

    private final String prefix;
    private final StringRedisSerializer stringSerializer = new StringRedisSerializer();

    public RedisPfxSerializer(String serviceName) {
        this.prefix = serviceName + ":";
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

}//EC
