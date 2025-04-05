package com.kmicro.order.service;

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
    private static final String PAYMENT_SERVICE_URL = "http://localhost:8095/api/payment";

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    public void proceedCheckOut(String userId) {

       try {
           // --------request CartService to send Cart Data
           List<OrderItemDTO> orderItemDTOList = getOrderItemsFromCartService(userId).get();

           // -------- Complete orderData in table
          OrderEntity saveOrder =  saveOrder(generateOrderEntity(orderItemDTOList));

           //-------- makePayment()
           Double totalAmount = getOrderTotalPrice(orderItemDTOList);

           makePayment(totalAmount, saveOrder.getId())
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
                   });
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

    public Mono<Map>makePayment(Double amount, Long  orderId) {
        WebClient client = WebClient.create(PAYMENT_SERVICE_URL);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount);
        requestBody.put("order_id", orderId);

        return client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);
    }

    private  OrderEntity generateOrderEntity( List<OrderItemDTO> orderItemDTOList){

     try {
         Double totalPrice = getOrderTotalPrice(orderItemDTOList);
         // ----------- generate  OrderEntity Object
         OrderEntity orderEntity = new OrderEntity();
         orderEntity.setOrderDate(LocalDateTime.now());
         orderEntity.setOrderTotal(totalPrice);
         orderEntity.setUserId(orderItemDTOList.getFirst().getUserId());
         orderEntity.setOrderStatus(OrderStatusEnum.PENDING);
         orderEntity.setPaymentMethod(PaymentMethodEnum.ONLINE);
         orderEntity.setTransactionId("transctionNumber_01");
         orderEntity.setTrackingNumber("trackingNumber_01");
         // -------- generate  OrderItemEntity Object
         List<OrderItemEntity> orderItemEntities = OrderItemMapper.mapDTOListToEntityList(orderItemDTOList, orderEntity);
         //----- --- set OrderItemEntity to OrderEntity
         orderEntity.setOrderItems(orderItemEntities);

         return orderEntity;
     }catch (Exception e){
         log.error(e.getMessage());
         e.printStackTrace();
         return null;
     }
}

    private Double getOrderTotalPrice(List<OrderItemDTO> orderItemDTOList) {
        Double totalPrice = 0.0;
        for (OrderItemDTO orderItemDTO : orderItemDTOList) {
            totalPrice += orderItemDTO.getPrice() * orderItemDTO.getQuantity();
        }
        return totalPrice;
    }

    @Transactional
    private OrderEntity saveOrder(OrderEntity orderEntity) {
            try {
               return  orderRepository.save(orderEntity);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
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

    public void removeItemFromOrder(Long orderItemID) {
        orderItemRepository.deleteById(orderItemID);
    }

}//EC

