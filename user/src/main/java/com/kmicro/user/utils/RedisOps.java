package com.kmicro.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.user.constants.AppContants;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.TokenEntity;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.exception.UserNotFoundException;
import com.kmicro.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class RedisOps {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final DBOps dbOps;

    RedisOps(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, DBOps dbOps){
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.dbOps = dbOps;
    }

    public void addVerification(String token, TokenEntity tokenEntity){
//        OrderDTO orderDTO = OrderMapper.mapEntityToDTOWithItems(order);
        redisTemplate.opsForValue().set(AppContants.REDIS_VERIFY_KEY_PREFIX + token, tokenEntity,
                Duration.ofMinutes(AppContants.EMAIL_VERIFY_TOKEN_DURATION));
//        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
    }

    public void deleteToken(TokenEntity tokenEntity, UserEntity userEntity) {
        redisTemplate.opsForValue().getAndDelete( AppContants.REDIS_VERIFY_KEY_PREFIX + userEntity.getId());
    }

    public void updateToken(TokenEntity tokenEntity){
        redisTemplate.opsForValue().set(AppContants.REDIS_VERIFY_KEY_PREFIX + tokenEntity.getToken(), tokenEntity,
                Duration.ofMinutes(AppContants.EMAIL_VERIFY_TOKEN_DURATION));
    }

    public TokenEntity getVerificationToken(String token) {
        Object verificationToken = redisTemplate.opsForValue().get(AppContants.REDIS_VERIFY_KEY_PREFIX + token);
        return null == verificationToken ? null : objectMapper.convertValue(verificationToken, TokenEntity.class);
    }

    @Cacheable(value = AppContants.CACHE_USER_KEY_PX, key = "#id")
    public UserDTO getCachedUser(Long id) {
        // This method ALWAYS fetches everything from DB
        UserEntity entity = dbOps.findUserByID(id)
                .orElseThrow(()-> new UserNotFoundException("User Not Found."));
        return UserMapper.EntityWithAddressToDTOWithAddress(entity);
    }
}//EC
