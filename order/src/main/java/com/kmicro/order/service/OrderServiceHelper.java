package com.kmicro.order.service;

import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.dtos.OrderItemDTO;
import com.kmicro.order.dtos.OrderStatusEnum;
import com.kmicro.order.dtos.PaymentMethodEnum;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.entities.OrderItemEntity;
import com.kmicro.order.mapper.OrderItemMapper;
import com.kmicro.order.mapper.OrderMapper;
import com.kmicro.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OrderServiceHelper {

    private static final String PAYMENT_SERVICE_URL = "http://localhost:8095/api/payment";
    private static final String USER_CART_URL = "http://localhost:8090/api/cart";
    private static final String TOPIC = "order-events";
    private static final String KEY_PREFIX = "order_";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Producer<String, String> kafkaProducer;

    @Transactional
    protected OrderEntity saveOrder(OrderEntity orderEntity, OrderRepository orderRepository) {
        log.info("Save order details in database and Redis");
        try {
            OrderEntity order =  orderRepository.save(orderEntity);
            saveOrderInRedis(order);
            return  order;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    protected   OrderEntity generateOrderEntity( List<OrderItemDTO> orderItemDTOList){
        log.info("create orderEntity from  orderItemDTOList");
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
            orderEntity.setPaymentStatus(OrderStatusEnum.PENDING.name());
            // -------- generate  OrderItemEntity Object
            List<OrderItemEntity> orderItemEntities = OrderItemMapper.mapDTOListToEntityList(orderItemDTOList, orderEntity);
            //----- --- set OrderItemEntity to OrderEntity
            orderEntity.setOrderItems(orderItemEntities);

            return orderEntity;
        }catch (Exception e){
            log.error(e.getMessage());
            log.debug("detailedMessage: {}",e.getStackTrace());
            return null;
        }
    }

    protected Double getOrderTotalPrice(List<OrderItemDTO> orderItemDTOList) {
        Double totalPrice = 0.0;
        for (OrderItemDTO orderItemDTO : orderItemDTOList) {
            totalPrice += orderItemDTO.getPrice() * orderItemDTO.getQuantity();
        }
        return totalPrice;
    }

    protected Mono<Void> makePayment(Double amount, Long  orderId) {
        log.info("request payment service to makePayment for orderID: {}",orderId);
        WebClient client = WebClient.create(PAYMENT_SERVICE_URL);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount);
        requestBody.put("order_id", orderId);

        return client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class);
    }

    protected Mono<Void> removeCart(String user_id) {
        log.info("request cart service to remove cart with ID: {}",user_id);
        WebClient client = WebClient.create(USER_CART_URL);
//        String userID = "/cart/"+user_id;
        return client.delete()
                .uri("/remove/"+user_id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    protected void saveOrderInRedis(OrderEntity order) {
        log.info("saveOrderInRedis with orderID: {}", order.getId());
        OrderDTO orderDTO = OrderMapper.mapEntityToDTOWithItems(order);
        String key = KEY_PREFIX + orderDTO.getId();
        redisTemplate.opsForValue().set(key, orderDTO);
    }

    protected Object getOrderFromRedis(String orderId){
        String key = KEY_PREFIX + orderId;
         return  redisTemplate.opsForValue().get(key);
    }

    protected  void sendNotificationToKafka(String key, String Data) {
        log.info("sendNotificationToKafka with orderID: {} and Data: {}", key,  Data);
        kafkaProducer.send(new ProducerRecord<String, String>(TOPIC, null, key, Data) );
    }


}// endClass
