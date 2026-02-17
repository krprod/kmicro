package com.kmicro.order.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.components.OutboxUtils;
import com.kmicro.order.constants.AppConstants;
import com.kmicro.order.constants.Status;
import com.kmicro.order.constants.PaymentMethod;
import com.kmicro.order.dtos.*;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.entities.OrderItemEntity;
import com.kmicro.order.entities.OutboxEntity;
import com.kmicro.order.exception.DataNotFoundException;
import com.kmicro.order.exception.OrderException;
import com.kmicro.order.mapper.OrderItemMapper;
import com.kmicro.order.mapper.OrderMapper;
import com.kmicro.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderUtils {

    private final RedisTemplate<String, Object> redisTemplate;
//    private final Producer<String, String> kafkaProducer;
    private  final OrderRepository orderRepository;
    private final KafkaUtils kafkaUtils;
    private final ObjectMapper objectMapper;
    private final OutboxUtils outboxUtils;
    private List<OutboxEntity> OutboxEventList = new ArrayList<>(10);

    @Transactional
    public OrderEntity saveOrder(OrderEntity orderEntity) {
            return orderRepository.save(orderEntity);
    }

    public OrderEntity generateOrderEntity( List<OrderItemDTO> orderItemDTOList){
        OrderEntity orderEntity = new OrderEntity();
        try {

            Double totalAmount = getSubTotalPrice(orderItemDTOList);

            // ----------- generate  OrderEntity Object

//            orderEntity.setOrderDate(LocalDateTime.now(ZoneId.of(AppConstants.TIME_ZONE_ID)));
//            orderEntity.setCreatedAt(LocalDateTime.now(ZoneId.of(AppConstants.TIME_ZONE_ID)));
//            orderEntity.setUpdatedAt(LocalDateTime.now(ZoneId.of(AppConstants.TIME_ZONE_ID)));
            orderEntity.setInitialTimeStamp(Instant.now());

            orderEntity.setSubtotal(totalAmount);
            orderEntity.setUserId(orderItemDTOList.getFirst().getUserId());
            orderEntity.setStatus(Status.PENDING);
            orderEntity.setPaymentMethod(PaymentMethod.ONLINE);
            orderEntity.setTransactionId(AppConstants.TEMP_TRANSACTION_ID);
            orderEntity.setTrackingNumber(AppConstants.TEMP_TRACKING_ID);
            orderEntity.setPaymentStatus(Status.PAYMENT_PENDING.name());

            orderEntity.setShippingFee(100.00);
            orderEntity.setTotalAmount(totalAmount + 100.00);
            // -------- generate  OrderItemEntity Object
            List<OrderItemEntity> orderItemEntities = OrderItemMapper.mapDTOListToEntityList(orderItemDTOList, orderEntity);

            //----- --- set OrderItemEntity to OrderEntity
            orderEntity.setOrderItems(orderItemEntities);

            log.info("Order Entity Generated Successfully");
//            return orderEntity;
        }catch (Exception e){
            log.error("Order Entity Generation Failed:{}",e.getMessage());
            log.debug("detailedMessage: {}",e.getStackTrace());
        }
        return orderEntity;
    }

    public Double getSubTotalPrice(List<OrderItemDTO> orderItemDTOList) {
        Double totalPrice = 0.0;
        for (OrderItemDTO orderItemDTO : orderItemDTOList) {
            totalPrice += orderItemDTO.getPrice() * orderItemDTO.getQuantity();
        }
        return totalPrice;
    }

    public Mono<Void> makePayment(ProcessPaymentRecord paymentRecord) {
        log.info("request payment service to makePayment for orderID: {}",paymentRecord.orderId());
        WebClient client = WebClient.create(AppConstants.PAYMENT_SERVICE_URL);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", paymentRecord.amount());
        requestBody.put("order_id", paymentRecord.orderId());
        requestBody.put("payment_method", paymentRecord.method());

        return client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> removeCart(String user_id) {
        log.info("request cart service to remove cart with ID: {}",user_id);
        WebClient client = WebClient.create(AppConstants.USER_CART_URL);
//        String userID = "/cart/"+user_id;
        return client.delete()
                .uri("/remove/"+user_id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public void saveOrderInRedis(OrderEntity order) {
        OrderDTO orderDTO = OrderMapper.mapEntityToDTOWithItems(order);
        String key = AppConstants.REDIS_ORDER_KEY_PREFIX + orderDTO.getId();
        redisTemplate.opsForValue().set(key, orderDTO);
        log.info("Order Saved In Redis ID: {}  and Key: {}", order.getId(), key);
    }

    public void saveOrderInRedis(OrderDTO order) {
        String key = AppConstants.REDIS_ORDER_KEY_PREFIX + order.getId();
        redisTemplate.opsForValue().set(key, order);
        log.info("Order Saved In Redis ID: {}  and Key: {}", order.getId(), key);
    }

    public Object getOrderFromRedis(String orderId){
        String key = AppConstants.REDIS_ORDER_KEY_PREFIX + orderId;
         return  redisTemplate.opsForValue().get(key);
    }

    public   void sendNotificationToKafka(String key, String Data) {
        log.info("sendNotificationToKafka with orderID: {} and Data: {}", key,  Data);
//        kafkaProducer.send(new ProducerRecord<String, String>(AppConstants.TOPIC, null, key, Data) );
    }

    @Cacheable(value = AppConstants.CACHE_PREFIX_USER, key = "#userId", unless = "#result == null")
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrdersListByUserIDFromDB(Long userId) {
        if(!orderRepository.existsByUserId(userId)){
            throw new DataNotFoundException("Order Not Found In DB for UserID:"+ userId);
        }
        log.info("Orders with  found for UserID: {}",userId);
        List<OrderEntity> orderEntity =   orderRepository.findByUserId(userId);
        List<OrderDTO> orderDTOList = OrderMapper.entityToDTOListWithItems(orderEntity);
        return new ArrayList<>(orderDTOList);
    }

    @Cacheable(value = AppConstants.CACHE_PREFIX_ORDER, key = "#orderId", unless = "#result == null")
    @Transactional(readOnly = true)
    public OrderDTO getAllOrderById(Long orderId) {
        if(!orderRepository.existsById(orderId)){
            throw new DataNotFoundException("Order Not Found In DB for OrderID:"+ orderId);
        }
        log.info("Orders with  found for OrderID: {}",orderId);
        return OrderMapper.mapEntityToDTOWithItems(orderRepository.findById(orderId).get(), objectMapper);
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderByIdFromDB(Long orderId) {
        if(!orderRepository.existsById(orderId)){
            throw new DataNotFoundException("Order Not Found In DB for OrderID:"+ orderId);
        }
        log.info("Orders with  found for OrderID: {}",orderId);
        return orderRepository.findById(orderId).get();
    }

    @Transactional
    public void saveOrderInDB(OrderEntity order){
        orderRepository.save(order);
    }

    @Transactional
    public OrderEntity changeOrderStatus(ChangeOrderStatusRec orderStatusRec) {
        OrderEntity order = orderRepository.findByIdAndUserId(orderStatusRec.orderID(), orderStatusRec.userID())
                .orElseThrow(()-> new OrderException("Order Not Exists By ID: {} for UserID: {}", orderStatusRec.orderID(), orderStatusRec.userID()));

        log.info("Changing Status from: {} to: {} of Order ID: {}",order.getStatus(), orderStatusRec.orderStatus(), order.getId());
        order.setStatus(Status.valueOf(orderStatusRec.orderStatus()));
        return orderRepository.save(order);
    }

    public void sendOrderEventToNotification(OrderDTO savedOrder) {
        try {
            String orderJson = objectMapper.writeValueAsString(savedOrder);
            kafkaUtils.sendOrderEvent(savedOrder.getId().toString(),orderJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void addEventToList(OutboxEntity event){
        try{
            this.OutboxEventList.add(event);
            log.info("Event Added In Outbox List: {}", event.toString());
        }catch (Exception e){
            log.info("Event Failed To Added In Outbox List: {}", event.toString());
        }
    }

    public List<OutboxEntity> getEventsList(){
        return this.OutboxEventList;
    }

    // Error Prone Method, Need To Handle Carefully
    public void purgeEventList(){
        int size = this.OutboxEventList.size();
        List<OutboxEntity> entityList = outboxUtils.saveAllEvents(this.OutboxEventList);

//        CronExpression ce = CronExpression.parse("0 */2 * * * *");
//        LocalDateTime next = ce.next(LocalDateTime.now());
//        System.out.println("Next execution will be: " + next);

        if( size == entityList.size()){
            this.OutboxEventList.clear();
        }
    }

    public OrderEntity generateOrderEntityWithAddress(CheckoutDetailsDTO orderAddress, List<OrderItemDTO> orderItemDTOList) {
        OrderEntity order = this.generateOrderEntity(orderItemDTOList);
        try {
            // set Order address
            order.setShippingAddress(objectMapper.writeValueAsString(orderAddress));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return  order;
    }
}// endClass
