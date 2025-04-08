package com.kmicro.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.dtos.OrderItemDTO;
import com.kmicro.order.dtos.OrderStatusEnum;
import com.kmicro.order.dtos.PaymentMethodEnum;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.entities.OrderItemEntity;
import com.kmicro.order.mapper.OrderItemMapper;
import com.kmicro.order.mapper.OrderMapper;
import com.kmicro.order.repository.OrderItemRepository;
import com.kmicro.order.repository.OrderRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrderService {

    private static final String USER_CART_URL = "http://localhost:8090/api/cart/";

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderServiceHelper orderServiceHelper;

    @Autowired
    private ObjectMapper objectMapper;


    public void proceedCheckOut(String userId) {

       try {
           // -------- GET Cart Data from cart-service
           List<OrderItemDTO> orderItemDTOList = getOrderItemsFromCartService(userId).get();

           // -------- save order in table
           OrderEntity generatedOrderEntity = orderServiceHelper.generateOrderEntity(orderItemDTOList);
          OrderEntity savedOrder =  orderServiceHelper.saveOrder(generatedOrderEntity, orderRepository);

           //-------- POST  order details to payment service
           Double totalAmount = orderServiceHelper.getOrderTotalPrice(orderItemDTOList);
           orderServiceHelper. makePayment(totalAmount, savedOrder.getId()).subscribe();

       /*    makePayment(totalAmount, saveOrder.getId())
                   .subscribe(response -> {
                       System.out.println("Payment response: " + response);
                       // Process the response here (e.g., update database, send notifications)
                       if(response.get("payment_status").equals("SUCCESS")){
                           String transactionId = (String) response.get("transaction_id");
                           int responseOrderId = (int) response.get("order_id");

                            // can add data in kafka here,
                           // now update the order table  with paymentStatus and TransactionID

                           // hit notification service
                       }

                   }, error -> {
                       System.err.println("Error processing payment: " + error.getMessage());
                       // Handle the error here
                   });*/
       }catch (Exception e){
           log.error(e.getMessage());
           e.printStackTrace();
       }
     /*   WebClient client = WebClient.create("http://localhost:8090/api");
        String userID = "/cart/"+userId;
      Flux<OrderItemDTO> response =  client.get().uri(userID).retrieve().bodyToFlux(OrderItemDTO.class);
        response.subscribe(
                orderItemDTO -> {
                    orderItemDTOList.add(orderItemDTO);
                }
        );*/
        //       WebClient.create("http://localhost:8090").build().get().retrieve();

    }

    private CompletableFuture<List<OrderItemDTO>> getOrderItemsFromCartService(String user_id) {
//        WebClient client = WebClient.create("your_base_url"); // Replace with your base URL
        WebClient client = WebClient.create(USER_CART_URL);
//        String userID = "/cart/"+user_id;
        Flux<OrderItemDTO> response = client.get()
                .uri(user_id)
                .retrieve()
                .bodyToFlux(OrderItemDTO.class);

        Mono<List<OrderItemDTO>> listMono = response.collectList();

        return listMono.toFuture();
    }

    public List<OrderDTO> getOrdersListByUserID(Long userId) {
        List<OrderEntity> orderEntity = orderRepository.findByUserId(userId);
        List<OrderDTO> orderDTO = null;

        if (orderEntity.size() > 0) {
            orderDTO = OrderMapper.mapEntityListToDTOList(orderEntity);
        }
        return orderDTO;
    }

    public OrderDTO getOrderDetailsByOrderID(Long orderID) {
        OrderEntity orderEntity = orderRepository.findById(orderID).get();
        OrderDTO orderDTO = null;
        if (orderEntity != null) {
            orderDTO = OrderMapper.mapEntityToDTOWithItems(orderEntity);
        }
        return orderDTO;
    }

    public OrderEntity updateOrderStatus(Long orderID, String transactionId, String paymentStatus) {
        OrderEntity orderEntity = orderRepository.findById(orderID).get();
        orderEntity.setTransactionId(transactionId);
        orderEntity.setPaymentStatus(paymentStatus);
        orderEntity.setOrderStatus(OrderStatusEnum.CONFIRMED);
        return orderRepository.save(orderEntity);
    }

    public String getOrderFromRedis(Long orderID) {
       try {
           Object cachedOrder =  orderServiceHelper.getOrderFromRedis(orderID.toString());
           return objectMapper.writeValueAsString(cachedOrder);
       } catch (JsonProcessingException e) {
           throw new RuntimeException(e);
       }
    }

    public void removeItemFromOrder(Long orderItemID) {
        orderItemRepository.deleteById(orderItemID);
    }
}//EC

