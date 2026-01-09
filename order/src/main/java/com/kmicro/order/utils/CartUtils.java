package com.kmicro.order.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.constants.AppConstants;
import com.kmicro.order.dtos.CartDTO;
import com.kmicro.order.dtos.OrderItemDTO;
import com.kmicro.order.exception.DataNotFoundException;
import com.kmicro.order.mapper.CartRedisMapper;
import com.kmicro.order.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartUtils {
    private final RedisTemplate<String, CartDTO> redisTemplate;
    private final ObjectMapper objectMapper;

    public List<OrderItemDTO> getCartItemAsOrderItemDTO(String userId){
        List<CartDTO> cartDataList = this.getCartList(userId);
        return OrderItemMapper.mapCartDtoToOrderItemList(cartDataList);
    }

    public List<CartDTO> getCartList(String userId){
        List<Object> cachedCart1 =  redisTemplate.opsForHash().values(AppConstants.REDIS_CART_KEY_PREFIX + userId);
        if(cachedCart1.isEmpty()){
            throw new DataNotFoundException("Cart Not Found for UserID: "+ userId);
        }
        log.info("Cart Found for  UserID:  {}", userId);
        return CartRedisMapper.getListOfProducts(cachedCart1, objectMapper);
    }

    private CompletableFuture<List<OrderItemDTO>> getOrderItemsFromCartService(String user_id) {
//        WebClient client = WebClient.create("your_base_url"); // Replace with your base URL
        log.info("request cart details from cart service for user ID: {}",user_id);
        WebClient client = WebClient.create(AppConstants.USER_CART_URL);
//        String userID = "/cart/"+user_id;
        Flux<OrderItemDTO> response = client.get()
                .uri(user_id)
                .retrieve()
                .bodyToFlux(OrderItemDTO.class);

        Mono<List<OrderItemDTO>> listMono = response.collectList();

        return listMono.toFuture();
    }
}
