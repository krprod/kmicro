package com.kmicro.order.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.service.OrderService;
import com.kmicro.order.utils.OrderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceListner {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderUtils orderUtils;

    @Transactional
    public void updatePaymentInfoInDB(JsonNode paymentJson) {
      try {
          Long orderId = paymentJson.get("order_id").asLong();
          String transactionID = paymentJson.get("trasaction_id").asText();
          String paymentStatus = paymentJson.get("payment_status").asText();

          // update order table record and Get User ID
          OrderEntity order = orderService.updateOrderStatus(orderId , transactionID,paymentStatus);
          orderUtils.saveOrderInRedis(order);

          // remove cart data from redis
          orderUtils.removeCart(order.getUserId().toString()).subscribe();

          // send Notification
         this.sendNotification(orderId);
          
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    }

    public void sendNotification(Long orderID) {
//        String orderString = orderService.getOrderFromRedis(orderID);
//        orderUtils.sendNotificationToKafka(orderID.toString(), orderString);
    }
}
