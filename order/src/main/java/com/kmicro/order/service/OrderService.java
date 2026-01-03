package com.kmicro.order.service;

import com.kmicro.order.Constants.AppConstants;
import com.kmicro.order.Constants.OrderStatus;
import com.kmicro.order.dtos.*;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.mapper.OrderMapper;
import com.kmicro.order.repository.OrderItemRepository;
import com.kmicro.order.repository.OrderRepository;
import com.kmicro.order.utils.CacheUtils;
import com.kmicro.order.utils.CartUtils;
import com.kmicro.order.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kmicro.order.Constants.AppConstants.ASIA_ZONE_ID;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private  final OrderRepository orderRepository;
    private  final OrderItemRepository orderItemRepository;
    private  final OrderUtils orderUtils;
    private final CartUtils cartUtils;
    private final CacheUtils cacheUtils;

    @Transactional
    public void proceedCheckOut(String userId) {

       try {
           // -------- GET Cart Data from cart-service
           List<OrderItemDTO> orderItemDTOList = cartUtils.getCartItemAsOrderItemDTO(userId);
            log.info("Order Item Fetched From Cart Successfully");

           // -------- Generate Order Entity
           OrderEntity generatedOrderEntity = orderUtils.generateOrderEntity(orderItemDTOList);
           log.info("Order Entity Generated Successfully");

           //--------  Save Order in DB, Redis, and
           ProcessPaymentRecord processPaymentRecord =  this.saveOrder(generatedOrderEntity);
           log.info("Order Save In DB Successfully");

           // ----------  Send Request to Payment Service To Start Processing
           log.info("Requesting Payment Service: {}", LocalDateTime.now(ASIA_ZONE_ID));
//           orderUtils. makePayment(processPaymentRecord).subscribe();
           log.info("Response From Payment Service: {}", LocalDateTime.now(ASIA_ZONE_ID));


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

   public ProcessPaymentRecord saveOrder(OrderEntity order){
        OrderEntity savedOrder = orderUtils.saveOrder(order);
        log.info("Order Saved In DB: {}",savedOrder.getId());

        //--- Instead directly saving Into Redis, Use Kafka For Lazy saveOrderInRedis
        orderUtils.saveOrderInRedis(savedOrder);

        return new ProcessPaymentRecord(
                savedOrder.getId(),
                savedOrder.getOrderTotal(),
                savedOrder.getPaymentMethod().name());
   }

    public OrderDTO getOrderDetailsByOrderID(Long orderID, Boolean withItems) {
        OrderEntity orderEntity = orderUtils.getAllOrdersListByIDFromDB(orderID);
        return withItems ?
                OrderMapper.mapEntityToDTOWithItems(orderEntity):
                OrderMapper.mapEntityToDTOWithoutItems(orderEntity);
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

    public List<OrderDTO> getAllOrdersListByUserID(Long userId, Boolean withItems) {
        List<OrderEntity> orderEntity = orderUtils.getAllOrdersListByUserIDFromDB(userId);
        return withItems ?
                OrderMapper.entityToDTOListWithItems(orderEntity)
                :  OrderMapper.entityToDTOListWithoutItems(orderEntity);
    }


    public ResponseDTO changeOrderStatus(ChangeOrderStatusRec orderStatusRec) {
              OrderEntity savedOrder = orderUtils.changeOrderStatus(orderStatusRec);

              // Save In Redis
              orderUtils.saveOrderInRedis(savedOrder);

              // Send Notification To User

        return new ResponseDTO("200","Order Status Changed Successfully");
    }

}//EC

