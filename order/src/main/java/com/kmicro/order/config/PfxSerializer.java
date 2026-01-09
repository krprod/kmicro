package com.kmicro.order.config;

import org.springframework.data.redis.serializer.StringRedisSerializer;

public class PfxSerializer extends StringRedisSerializer {
//public class PfxSerializer implements RedisSerializer<Object> {
    private final String prefix;
    private final StringRedisSerializer stringSerializer = new StringRedisSerializer();

    public PfxSerializer(String serviceName) {
        this.prefix = serviceName + ":";
    }

   /* @Override
    public byte[] serialize(Object key) throws SerializationException {
        if (key == null) {
            return null;
        }
        // Convert the key (String, Long, or SimpleKey) to a string representation
        String keyString = prefix + key.toString();
        return stringSerializer.serialize(keyString);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        String key = stringSerializer.deserialize(bytes);
        if (key != null && key.startsWith(prefix)) {
            return key.substring(prefix.length());
        }
        return key;
    }
*/

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
