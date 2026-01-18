package com.kmicro.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.components.OutboxUtils;
import com.kmicro.order.constants.AppConstants;
import com.kmicro.order.constants.OrderStatus;
import com.kmicro.order.dtos.*;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.entities.OutboxEntity;
import com.kmicro.order.mapper.OrderMapper;
import com.kmicro.order.repository.OrderItemRepository;
import com.kmicro.order.repository.OrderRepository;
import com.kmicro.order.utils.CacheUtils;
import com.kmicro.order.utils.CartUtils;
import com.kmicro.order.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kmicro.order.constants.AppConstants.ASIA_ZONE_ID;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private  final OrderRepository orderRepository;
    private  final OrderItemRepository orderItemRepository;
    private  final OrderUtils orderUtils;
    private final CartUtils cartUtils;
    private final CacheUtils cacheUtils;
    private final OutboxUtils outboxUtils;
    private final ObjectMapper objectMapper;
    private List<OutboxEntity> OutboxEventList = new ArrayList<>(10);
//    @Transactional
    public void proceedCheckOut(String userId) {

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
           log.error("Exception Occured at proceedCheckOut: {}",e.getMessage());
           log.debug("detailedMessage: {}",e.getStackTrace());
           e.printStackTrace();
           throw new RuntimeException("Something Went Wrong!");
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

   public OrderEntity saveOrder(OrderEntity order){
        OrderEntity savedOrder = orderUtils.saveOrder(order);
        log.info("Order Saved In DB: {}",savedOrder.getId());

        //--- Instead directly saving Into Redis, Use Kafka For Lazy saveOrderInRedis
        orderUtils.saveOrderInRedis(savedOrder);

        return savedOrder;
   }

   public ProcessPaymentRecord getPaymentRecordOfSavedOrder(OrderEntity order){
       return  new ProcessPaymentRecord(
               order.getId(),
               order.getTotalAmount(),
               order.getPaymentMethod().name(),
               order.getUserId(),
               order.getShippingFee());
   }

    @Cacheable(value = AppConstants.CACHE_PREFIX_ORDER, key = "#orderID")
    public OrderDTO getOrderDetailsByOrderID(Long orderID, Boolean withItems) {
        OrderEntity orderEntity = orderUtils.getAllOrdersListByIDFromDB(orderID);
        return withItems ?
                OrderMapper.mapEntityToDTOWithItems(orderEntity, objectMapper):
                OrderMapper.mapEntityToDTOWithoutItems(orderEntity,objectMapper);
    }

    public OrderEntity updateOrderStatus(Long orderID, String transactionId, String paymentStatus) {
        log.info("Saving orderID: {}, transactionId: {}, paymentStatus: {} in Database",orderID, transactionId,  paymentStatus);
        OrderEntity orderEntity = orderRepository.findById(orderID).get();
        orderEntity.setTransactionId(transactionId);
        orderEntity.setPaymentStatus(paymentStatus);
        //**  payment Status pr order status update krna hai if fail to fail_payment else processing
//        if(paymentStatus.toLowerCase().equals("success"))
        orderEntity.setOrderStatus(OrderStatus.PROCESSING);
        return orderRepository.save(orderEntity);
    }

    public OrderDTO getOrderFromCache(Long orderID) {
           OrderDTO cachedOrder =  cacheUtils.get(AppConstants.REDIS_ORDER_KEY_PREFIX + orderID.toString(), OrderDTO.class);
           log.info("Order Found In Redis for Key: {}",AppConstants.REDIS_ORDER_KEY_PREFIX + orderID.toString());
           return cachedOrder;
    }

    public void removeItemFromOrder(Long orderItemID) {
        orderItemRepository.deleteById(orderItemID);
    }

    @Cacheable(value = AppConstants.CACHE_PREFIX_USER, key = "#userId")
    public List<OrderDTO> getAllOrdersListByUserID(Long userId, Boolean withItems) {
        List<OrderEntity> orderEntity = orderUtils.getAllOrdersListByUserIDFromDB(userId);
        List<OrderDTO> dtos =withItems ?
                OrderMapper.entityToDTOListWithItems(orderEntity,objectMapper)
                :  OrderMapper.entityToDTOListWithoutItems(orderEntity,objectMapper);
        return new ArrayList<>(dtos);
    }

   @Caching(evict = {
           @CacheEvict(value = AppConstants.CACHE_PREFIX_ORDER, key = "#orderStatusRec.orderID"),
           @CacheEvict(value = AppConstants.CACHE_PREFIX_USER, key = "#orderStatusRec.userID")
   })
    public ResponseDTO changeOrderStatus(ChangeOrderStatusRec orderStatusRec) {
              OrderEntity savedOrder = orderUtils.changeOrderStatus(orderStatusRec);

              // Save In Redis
//              orderUtils.saveOrderInRedis(savedOrder);

              // Send Notification To User
               this.OutboxEventList.add(
                       outboxUtils.generatePendingEvent(
        //                            orderTransport,
                               OrderMapper.mapDynmicFieldOrderStatusUpdate(savedOrder, objectMapper),
                               savedOrder.getId().toString(),
                               AppConstants.ORDER_TOPIC,
                               AppConstants.EVENT_TYPES.get("ORDER_STATUS_UPDATE"),
                               AppConstants.SOURCE_SYSTEMS.get("NOTIFICATION")
                       )
               );
       //------------------- Saving Data In DB So Kafka Can Consume From that
       log.info("Start Saving Events In Outbox Table: {}", LocalDateTime.now(ASIA_ZONE_ID));
       int size = this.OutboxEventList.size();
       List<OutboxEntity> entityList = outboxUtils.saveAllEvents(this.OutboxEventList);
       if( size == entityList.size()){
           this.OutboxEventList.clear();
       }
//           this.orderUtils.purgeEventList();
       log.info("End Events In Outbox Table: {}", LocalDateTime.now(ASIA_ZONE_ID));

        return new ResponseDTO("200","Order Status Changed Successfully");
    }

    @CacheEvict(value = AppConstants.CACHE_PREFIX_USER, key = "#userId")
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
            ProcessPaymentRecord processPaymentRecord =  this.getPaymentRecordOfSavedOrder(generatedOrderEntity);

            this.OutboxEventList.add(
                    outboxUtils.generatePendingEvent(
                            processPaymentRecord,
                            processPaymentRecord.orderId().toString(),
                            AppConstants.PAYMENT_TOPIC,
                            AppConstants.EVENT_TYPES.get("PAYMENT_REQ"),
                            AppConstants.SOURCE_SYSTEMS.get("PAYMENT")
                            )
            );

            // ----------  Send Notification -- New Order Created
            log.info("Generating Event for Notification Service: {}", LocalDateTime.now(ASIA_ZONE_ID));

            OrderDTO orderTransport = OrderMapper.mapEntityToDTOWithItems(savedOrder, objectMapper);
//            orderTransport.setShippingAddress(objectMapper.readValue(savedOrder.getShippingAddress(), OrderAddressDTO.class));

            this.OutboxEventList.add(
                    outboxUtils.generatePendingEvent(
//                            orderTransport,
                            OrderMapper.mapDynmicFieldOrderConfirmation(savedOrder, objectMapper),
                            savedOrder.getId().toString(),
                            AppConstants.ORDER_TOPIC,
                            AppConstants.EVENT_TYPES.get("ORDER_CONFIRM"),
                            AppConstants.SOURCE_SYSTEMS.get("NOTIFICATION")
                    )
            );

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
    }
}//EC

