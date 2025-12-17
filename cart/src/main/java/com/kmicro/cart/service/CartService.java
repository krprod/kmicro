package com.kmicro.cart.service;

import com.kmicro.cart.dtos.CartDTO;
import com.kmicro.cart.mapper.CartRedisMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CartService {

    private static final String CACHE_KEY_PREFIX = "CART_";

    @Autowired
    private RedisTemplate<String, CartDTO>  redisTemplate;

//    @Autowired
//    private RedisTemplate<String, CartDTO> redisTemplate1;

    public  String  getAllCarts() {
         /*HashOperations<String, String, CartDTO> cachedCart =  redisTemplate1.opsForHash();
        cachedCart.entries("CART_4");*/
//        Map<Object, Object> cachedCart =  redisTemplate.opsForHash().entries();
        return "cachedCart";
    }

    public List<CartDTO> getCartDetails(String userId) {
        log.info("getting cart details for user ID:  {}", userId);
        String cacheKey = getCacheKeyPrefix(userId.toString());
        Map<Object, Object> cachedCart =  redisTemplate.opsForHash().entries(cacheKey);
        return CartRedisMapper.getListOfProducts(cachedCart);
    }

    public void addUpdateCart(CartDTO cartDTO) {
        log.info("add-update cart request DTO: {}", cartDTO.toString());
        String cacheKey = getCacheKeyPrefix( cartDTO.getUserId().toString());
        redisTemplate.opsForHash().put(cacheKey, cartDTO.getProductId().toString(), cartDTO);
    }

    public Long removeFromCart(CartDTO cartDTO) {
        log.info("remove item from cart request DTO: {}", cartDTO.toString());
        String cacheKey = getCacheKeyPrefix(cartDTO.getUserId().toString());
        return  redisTemplate.opsForHash().delete(cacheKey, cartDTO.getProductId().toString());
    }

    public Boolean deleteCart(String userID) {
        log.info("deleting cart ID: {}",CACHE_KEY_PREFIX+ userID);
        String cacheKey = getCacheKeyPrefix(userID);
        return  redisTemplate.delete(cacheKey);
    }

    private  String getCacheKeyPrefix(String id){
        return  CACHE_KEY_PREFIX + id;
    }
}
