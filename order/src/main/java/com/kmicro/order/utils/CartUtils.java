package com.kmicro.order.utils;

import com.kmicro.order.Constants.AppConstants;
import com.kmicro.order.dtos.CartDTO;
import com.kmicro.order.dtos.OrderItemDTO;
import com.kmicro.order.mapper.OrderItemMapper;
import com.kmicro.order.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final CartService cartService;

    public List<OrderItemDTO> getCartItemAsOrderItemDTO(String userId){
        List<CartDTO> cartDataList = cartService.getCartByUserID(userId);
        return OrderItemMapper.mapCartDtoToOrderItemList(cartDataList);
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
