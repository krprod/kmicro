package com.kmicro.order.service;

import com.kmicro.order.constants.AppConstants;
import com.kmicro.order.dtos.CartDTO;
import com.kmicro.order.exception.DataNotFoundException;
import com.kmicro.order.utils.CartUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CartService {
    private final RedisTemplate<String, CartDTO> redisTemplate;
    private final CartUtils cartUtils;

    public  String  getAllCarts() {
         /*HashOperations<String, String, CartDTO> cachedCart =  redisTemplate1.opsForHash();
        cachedCart.entries("CART_4");*/
//        Map<Object, Object> cachedCart =  redisTemplate.opsForHash().entries();
        return "cachedCart";
    }

    public List<CartDTO> getCartByUserID(String userId) {
//        Map<Object, Object> cachedCart =  redisTemplate.opsForHash().entries(cacheKey);
            return cartUtils.getCartList(userId);
    }

    public void addUpdateCart(CartDTO cartDTO) {
        redisTemplate.opsForHash().put(
                        AppConstants.REDIS_CART_KEY_PREFIX + cartDTO.getUserId(),
                        cartDTO.getProductId().toString(),
                        cartDTO);
        log.info("Cart add-update request succeed with data: {}", cartDTO.toString());
//        log.debug("Cart add-update request succeed with data: {}", cartDTO.toString());
    }

    public void removeItemFromCart(CartDTO cartDTO) {
        Long deletedCount = redisTemplate.opsForHash().delete(
                AppConstants.REDIS_CART_KEY_PREFIX + cartDTO.getUserId(),
                                cartDTO.getProductId().toString());

        if (deletedCount == 0) {
            throw new DataNotFoundException("Item Not Found In Cart: "+ cartDTO.getProductId());
        }
        log.info("Item removed remove cart Successfully: {}", cartDTO.getProductId());
    }

    public void deleteCart(String userID) {
        Boolean deletedCount = redisTemplate.delete(AppConstants.REDIS_CART_KEY_PREFIX + userID);
        if(!deletedCount){
            throw new DataNotFoundException("Cart Not Exists of ID: "+AppConstants.REDIS_CART_KEY_PREFIX + userID);
        }
        log.info("deleting cart ID: {}",AppConstants.REDIS_CART_KEY_PREFIX + userID);
    }

}//EC
