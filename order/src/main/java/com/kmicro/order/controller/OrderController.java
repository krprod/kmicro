package com.kmicro.order.controller;

import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.dtos.OrderItemDTO;
import com.kmicro.order.service.OrderService;
import lombok.Getter;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<String>   checkOut(@PathVariable(value = "userId") String userId){
        orderService.proceedCheckOut(userId);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersListByUserID(@PathVariable(value = "userId") Long userId) {
        List<OrderDTO> orderDTOList = orderService.getOrdersListByUserID(userId);
        return null != orderDTOList ? ResponseEntity.ok(orderDTOList) : ResponseEntity.status(404).body(null);
    }

    @GetMapping("/order/{orderID}")
    public ResponseEntity<OrderDTO> getOrderDetailsByOrderID(@PathVariable(value = "orderID") Long orderID) {
       OrderDTO orderDTO = orderService.getOrderDetailsByOrderID(orderID);
        return  null != orderDTO ? ResponseEntity.ok(orderDTO) :   ResponseEntity.status(404).body(null);
    }

    @GetMapping("/order/cache/{orderID}")
    public ResponseEntity<String> getOrderFromRedis(@PathVariable(value = "orderID") Long orderID) {
       String  cachedOrder =  orderService.getOrderFromRedis(orderID);
        return  true ? ResponseEntity.ok(cachedOrder) :   ResponseEntity.status(404).body(null);
    }

    // remove full order


    // remove order-item from order
//    @DeleteMapping("/order/{orderID}/orderitem/{orderItemID}")
//    public ResponseEntity<String> removeOrderItemFromOrder(@PathVariable(value = "orderID") Long orderID, @PathVariable(value = "orderItemID") Long orderItemID) {
//        return ResponseEntity.ok("success");
//    }
    /*@DeleteMapping("/removeItem/{orderItemID}")
    public ResponseEntity<String> removeItemFromOrder(@PathVariable(value = "orderItemID") Long orderItemID) {
        orderService.removeItemFromOrder(orderItemID);
        return ResponseEntity.ok("success");
    }*/


    // update order item
//    @PutMapping("/updateOrderItem")
//    public  ResponseEntity<String>

}//EC
