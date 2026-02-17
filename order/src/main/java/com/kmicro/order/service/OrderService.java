package com.kmicro.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.constants.AppConstants;
import com.kmicro.order.constants.KafkaConstants;
import com.kmicro.order.constants.Status;
import com.kmicro.order.dtos.ChangeOrderStatusRec;
import com.kmicro.order.dtos.CheckoutDetailsDTO;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.dtos.OrderItemDTO;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.kafka.producers.ExternalEventProducer;
import com.kmicro.order.kafka.producers.InternalEventProducer;
import com.kmicro.order.mapper.OrderMapper;
import com.kmicro.order.repository.OrderItemRepository;
import com.kmicro.order.repository.OrderRepository;
import com.kmicro.order.utils.CacheUtils;
import com.kmicro.order.utils.CartUtils;
import com.kmicro.order.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kmicro.order.constants.AppConstants.ASIA_ZONE_ID;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private  final OrderRepository orderRepository;
    private  final OrderItemRepository orderItemRepository;
    private final ExternalEventProducer externalEventProducer;
    private final InternalEventProducer internalEventProducer;
    private  final OrderUtils orderUtils;
    private final CartUtils cartUtils;
    private final CacheUtils cacheUtils;
    private final ObjectMapper objectMapper;


    public OrderDTO getOrderDetailsByOrderID(Long orderId, Boolean withItems) {
        OrderDTO orderDTO = orderUtils.getAllOrderById(orderId);
        if(!withItems){
            orderDTO.setOrderItems(null);
        }
       return orderDTO;
    }

    public OrderEntity updateOrderStatus(Long orderID, String transactionId, String paymentStatus) {
        log.info("Saving orderID: {}, transactionId: {}, paymentStatus: {} in Database",orderID, transactionId,  paymentStatus);
        OrderEntity orderEntity = orderRepository.findById(orderID).get();
        orderEntity.setTransactionId(transactionId);
        orderEntity.setPaymentStatus(paymentStatus);
        //**  payment Status pr order status update krna hai if fail to fail_payment else processing
//        if(paymentStatus.toLowerCase().equals("success"))
        orderEntity.setStatus(Status.PROCESSING);
        return orderRepository.save(orderEntity);
    }

    public List<OrderDTO> getAllOrdersListByUserID(Long userId, Boolean withItems) {
        List<OrderDTO> orderDtoList = orderUtils.getAllOrdersListByUserIDFromDB(userId);
        if(!withItems){
            orderDtoList.forEach(order->order.setOrderItems(null));
        }
        return orderDtoList;
     /*  return withItems ?
                    orderDtoList
                :  orderDtoList.stream().peek(order-> order.setOrderItems(null)).collect(Collectors.toList());*/
    }

   @Caching(
           evict = {@CacheEvict(value = AppConstants.CACHE_PREFIX_USER, key = "#orderStatusRec.userID")},
           put = {@CachePut(value = AppConstants.CACHE_PREFIX_ORDER, key = "#orderStatusRec.orderID")}
   )
    public OrderDTO changeOrderStatus(ChangeOrderStatusRec orderStatusRec) {
        OrderEntity savedOrder = orderUtils.changeOrderStatus(orderStatusRec);

        // Send Notification To User
       externalEventProducer.orderNTF(
               OrderMapper.mapEntityToDTOWithItems(savedOrder, objectMapper),
               KafkaConstants.ET_ORDER_STATUS_UPDATED,
               false
       );

       log.info("End Events In Outbox Table: {}", LocalDateTime.now(ASIA_ZONE_ID));

       return OrderMapper.mapEntityToDTOWithItems(savedOrder, objectMapper);
//        return new ResponseDTO("200","Order Status Changed Successfully");
    }

    @CacheEvict(value = AppConstants.CACHE_PREFIX_USER, key = "#userId")
    @Transactional
    public OrderDTO proceedCheckoutWithAddress(String userId, CheckoutDetailsDTO orderAddress) {
        try {
            // -------- GET Cart Data from cart-service
            List<OrderItemDTO> orderItemDTOList = cartUtils.getCartItemAsOrderItemDTO(userId);
            log.info("Order Item Fetched From Cart Successfully");

            // -------- Generate Order Entity
            OrderEntity generatedOrderEntity =  orderUtils.generateOrderEntityWithAddress(orderAddress, orderItemDTOList);
            log.info("Order Entity Generated Successfully");

            //--------  Save Order in DB, Redis, and
            OrderEntity savedOrder =orderRepository.save(generatedOrderEntity);
            log.info("Order Saved In DB: {}",savedOrder.getId());

            OrderDTO orderDTO = OrderMapper.mapEntityToDTOWithItems(savedOrder, objectMapper);
            orderUtils.saveOrderInRedis(orderDTO);

//            externalEventProducer.orderNTF(orderDTO, KafkaConstants.ET_ORDER_CREATED, true);
            internalEventProducer.requestPayment(orderDTO, KafkaConstants.ET_NEW_PAYMENT_REQ);

            return orderDTO;
        } catch (Exception e) {
            log.error("Exception Occured at proceedCheckOut: {}",e.getMessage());
            log.debug("detailedMessage: {}",e.getStackTrace());
            e.printStackTrace();
            throw new RuntimeException("Something Went Wrong!");
        }
    }

    @Caching(
            evict = {
                    @CacheEvict(value = AppConstants.CACHE_PREFIX_USER, key = "#result.userId"),
                    @CacheEvict(value = AppConstants.CACHE_PREFIX_ORDER, key = "#result.Id")
            })
    public OrderDTO proceedCheckoutRetry(Long orderId) {
        OrderEntity orderEntity = orderUtils.getOrderByIdFromDB(orderId);

        orderEntity.setStatus(Status.PAYMENT_RETRY);
        OrderDTO orderDTO = OrderMapper.mapEntityToDTOWithItems(orderEntity, objectMapper);
        orderUtils.saveOrderInRedis(orderDTO);

        internalEventProducer.requestPayment(orderDTO, KafkaConstants.ET_NEW_PAYMENT_REQ);
        return orderDTO;
    }


    public OrderDTO getOrderFromCache(Long orderID) {
        OrderDTO cachedOrder =  cacheUtils.get(AppConstants.REDIS_ORDER_KEY_PREFIX + orderID.toString(), OrderDTO.class);
        log.info("Order Found In Redis for Key: {}",AppConstants.REDIS_ORDER_KEY_PREFIX + orderID.toString());
        return cachedOrder;
    }
    public void removeItemFromOrder(Long orderItemID) {
        orderItemRepository.deleteById(orderItemID);
    }


/*   public ProcessPaymentRecord getPaymentRecordOfSavedOrder(OrderEntity order){
       return  new ProcessPaymentRecord(
               order.getId(),
               order.getTotalAmount(),
               order.getPaymentMethod().name(),
               order.getUserId(),
               order.getShippingFee());
   }*/

  /*  @CacheEvict(value = AppConstants.CACHE_PREFIX_USER, key = "#userId")
    public void proceedCheckoutWithAddress(String userId, OrderAddressDTO orderAddress) {
        try {
            // -------- GET Cart Data from cart-service
            List<OrderItemDTO> orderItemDTOList = cartUtils.getCartItemAsOrderItemDTO(userId);
            log.info("Order Item Fetched From Cart Successfully");

            // -------- Generate Order Entity
            OrderEntity generatedOrderEntity =  orderUtils.generateOrderEntityWithAddress(orderAddress, orderItemDTOList);
            log.info("Order Entity Generated Successfully");

            //--------  Save Order in DB, Redis, and
            OrderEntity savedOrder = orderUtils.saveOrder(generatedOrderEntity);
            orderUtils.saveOrderInRedis(savedOrder);
            log.info("Order Saved In DB: {}",savedOrder.getId());

            // ----------  Send Request to Payment Service To Start Processing
            log.info("Generating Event for Payment Service: {}", LocalDateTime.now(ASIA_ZONE_ID));
       *//*     ProcessPaymentRecord processPaymentRecord =  this.getPaymentRecordOfSavedOrder(generatedOrderEntity);
            this.OutboxEventList.add(
                    outboxUtils.generatePendingEvent(
                            processPaymentRecord,
                            processPaymentRecord.orderId().toString(),
                            KafkaConstants.PAYMENT_TOPIC,
                            KafkaConstants.ET_NEW_PAYMENT_REQ,
                            KafkaConstants.SYSTEM_PAYMENT
                            )
            );*//*
            externalEventProducer.requestPayment(savedOrder, KafkaConstants.ET_NEW_PAYMENT_REQ);
            // ----------  Send Notification -- New Order Created
            log.info("Generating Event for Notification Service: {}", LocalDateTime.now(ASIA_ZONE_ID));

            externalEventProducer.orderNTF(savedOrder, KafkaConstants.ET_ORDER_CONFIRMERD);

*//*            this.OutboxEventList.add(
                    outboxUtils.generatePendingEvent(
                            OrderMapper.mapDynmicFieldOrderConfirmation(savedOrder, objectMapper),
                            savedOrder.getId().toString(),
                            KafkaConstants.ORDER_TOPIC,
                            KafkaConstants.ET_ORDER_CONFIRMERD,
                            KafkaConstants.SYSTEM_NOTIFICATION
                    )
            );*//*

            //------------------- Saving Data In DB So Kafka Can Consume From that
            log.info("Start Saving Events In Outbox Table: {}", LocalDateTime.now(ASIA_ZONE_ID));
            int size = this.OutboxEventList.size();
            List<OutboxEntity> entityList = outboxUtils.saveAllEvents(this.OutboxEventList);
            if( size == entityList.size()){
                this.OutboxEventList.clear();
            }
//           this.orderUtils.purgeEventList();
            log.info("End Events In Outbox Table: {}", LocalDateTime.now(ASIA_ZONE_ID));
        } catch (Exception e) {
            log.error("Exception Occured at proceedCheckOut: {}",e.getMessage());
            log.debug("detailedMessage: {}",e.getStackTrace());
            e.printStackTrace();
            throw new RuntimeException("Something Went Wrong!");
        }
    }*/

    //    @Transactional
/*    public void proceedCheckOut(String userId) {

        try {
            // -------- GET Cart Data from cart-service
            List<OrderItemDTO> orderItemDTOList = cartUtils.getCartItemAsOrderItemDTO(userId);
            log.info("Order Item Fetched From Cart Successfully");

            // -------- Generate Order Entity
            OrderEntity generatedOrderEntity = orderUtils.generateOrderEntity(orderItemDTOList);
            log.info("Order Entity Generated Successfully");

            //--------  Save Order in DB, Redis, and
            OrderEntity savedOrder = orderUtils.saveOrder(generatedOrderEntity);
            log.info("Order Saved In DB: {}",savedOrder.getId());

            // ----------  Send Request to Payment Service To Start Processing
            log.info("Generating Event for Payment Service: {}", LocalDateTime.now(ASIA_ZONE_ID));
            ProcessPaymentRecord processPaymentRecord =  this.getPaymentRecordOfSavedOrder(generatedOrderEntity);

            this.OutboxEventList.add(
                    outboxUtils.generatePendingEvent(
                            processPaymentRecord,
                            processPaymentRecord.orderId().toString(),
                            "payment-events")
            );


//           orderUtils. makePayment(processPaymentRecord).subscribe();
//           log.info("Response From Payment Service: {}", LocalDateTime.now(ASIA_ZONE_ID));

            // ----------  Send Notification -- New Order Created
            log.info("Generating Event for Notification Service: {}", LocalDateTime.now(ASIA_ZONE_ID));
            this.OutboxEventList.add(
                    outboxUtils.generatePendingEvent(
//                           OrderMapper.mapEntityToDTOWithItems(savedOrder),
                            OrderMapper.mapDynmicFieldOrderConfirmation(savedOrder, objectMapper),
                            savedOrder.getId().toString(),
                            "t-order-placed")
            );
//           log.info("Request to Notification Service: {}", LocalDateTime.now(ASIA_ZONE_ID));
//            orderUtils.sendOrderEventToNotification(OrderMapper.mapEntityToDTOWithItems(savedOrder));

            //------------------- Saving Data In DB So Kafka Can Consume From that
            log.info("Start Saving Events In Outbox Table: {}", LocalDateTime.now(ASIA_ZONE_ID));
            int size = this.OutboxEventList.size();
            List<OutboxEntity> entityList = outboxUtils.saveAllEvents(this.OutboxEventList);
            if( size == entityList.size()){
                this.OutboxEventList.clear();
            }
//           this.orderUtils.purgeEventList();
            log.info("End Events In Outbox Table: {}", LocalDateTime.now(ASIA_ZONE_ID));

       *//*    makePayment(totalAmount, saveOrder.getId())
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
                   });*//*
        }catch (Exception e){
            log.error("Exception Occured at proceedCheckOut: {}",e.getMessage());
            log.debug("detailedMessage: {}",e.getStackTrace());
            e.printStackTrace();
            throw new RuntimeException("Something Went Wrong!");
        }
     *//*   WebClient client = WebClient.create("http://localhost:8090/api");
        String userID = "/cart/"+userId;
      Flux<OrderItemDTO> response =  client.get().uri(userID).retrieve().bodyToFlux(OrderItemDTO.class);
        response.subscribe(
                orderItemDTO -> {
                    orderItemDTOList.add(orderItemDTO);
                }
        );*//*
        //       WebClient.create("http://localhost:8090").build().get().retrieve();

    }*/

/*   public OrderEntity saveOrder(OrderEntity order){
        OrderEntity savedOrder = orderUtils.saveOrder(order);
        log.info("Order Saved In DB: {}",savedOrder.getId());

        //--- Instead directly saving Into Redis, Use Kafka For Lazy saveOrderInRedis
        orderUtils.saveOrderInRedis(savedOrder);

        return savedOrder;
   }*/
}//EC

