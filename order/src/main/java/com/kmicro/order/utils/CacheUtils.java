package com.kmicro.order.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.dtos.CartDTO;
import com.kmicro.order.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheUtils {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplateClassCast;
    private final RedisTemplate<String, CartDTO> redisTemplateCart;

    public <T> T get(String key, Class<T> targetClass) {
        Object value = redisTemplateClassCast.opsForValue().get(key);
        if (value == null) throw new DataNotFoundException("Data Not Found In Redis for Key: {}",key);

        // If it's already the right type, return it; otherwise, convert it
        return targetClass.isInstance(value)
                ? targetClass.cast(value)
                : objectMapper.convertValue(value, targetClass);
    }

}
