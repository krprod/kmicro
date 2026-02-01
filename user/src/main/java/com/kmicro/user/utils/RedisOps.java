package com.kmicro.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.constants.AppContants;
import com.kmicro.user.entities.TokenVerification;
import com.kmicro.user.entities.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class RedisOps {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    RedisOps(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper){
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void addVerification(String token, TokenVerification tokenVerification){
//        OrderDTO orderDTO = OrderMapper.mapEntityToDTOWithItems(order);
        redisTemplate.opsForValue().set(AppContants.REDIS_VERIFY_KEY_PREFIX + token, tokenVerification,
                Duration.ofMinutes(AppContants.EMAIL_VERIFY_TOKEN_DURATION));
//        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
    }

    public void deleteToken(TokenVerification tokenVerification, UserEntity userEntity) {
        redisTemplate.opsForValue().getAndDelete( AppContants.REDIS_VERIFY_KEY_PREFIX + userEntity.getId());
    }

    public void updateToken(TokenVerification tokenVerification, UserEntity userEntity){
        redisTemplate.opsForValue().set(AppContants.REDIS_VERIFY_KEY_PREFIX + tokenVerification.getToken(),tokenVerification,
                Duration.ofMinutes(AppContants.EMAIL_VERIFY_TOKEN_DURATION));
    }

    public TokenVerification getVerificationToken(String token) {
        Object verificationToken = redisTemplate.opsForValue().get(AppContants.REDIS_VERIFY_KEY_PREFIX + token);
        return null == verificationToken ? null : objectMapper.convertValue(verificationToken,TokenVerification.class);
    }
}//EC
