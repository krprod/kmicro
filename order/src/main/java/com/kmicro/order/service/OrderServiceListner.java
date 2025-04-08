package com.kmicro.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kmicro.order.entities.OrderEntity;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceListner {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderServiceHelper orderServiceHelper;

    @Transactional
    public void updatePaymentInfoInDB(JsonNode paymentJson) {
      try {
          Long orderId = paymentJson.get("order_id").asLong();
          String transactionID = paymentJson.get("trasaction_id").asText();
          String status = paymentJson.get("payment_status").asText();

          // update order table record and Get User ID
          OrderEntity order = orderService.updateOrderStatus(orderId , transactionID,status);
          orderServiceHelper.saveOrderInRedis(order);

          // remove cart data from redis
          orderServiceHelper.removeCart(order.getUserId().toString()).subscribe();

          // send Notification
         this.sendNotification(orderId);
          
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    }

    public void sendNotification(Long orderID) {
        String orderString = orderService.getOrderFromRedis(orderID);
        orderServiceHelper.sendNotificationToKafka(orderID.toString(), orderString);
    }
}
