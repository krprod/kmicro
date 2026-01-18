package com.kmicro.order.controller;

import com.kmicro.order.dtos.ChangeOrderStatusRec;
import com.kmicro.order.dtos.OrderAddressDTO;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.dtos.ResponseDTO;
import com.kmicro.order.service.OrderService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Controller", description = "Operations for Order lifecycle")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Operation(summary = "Process Checkout and Proceed For Payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order Created Successful, Waiting For Payment"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<String> checkOut(
            @RequestBody(required = false) OrderAddressDTO orderAddress,
            @PathVariable(value = "userId") String userId) {
//        orderService.proceedCheckOut(userId);
        orderService.proceedCheckoutWithAddress(userId, orderAddress);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "Get All Orders of the User by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of  Order Retrieved Successfully"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersListByUserID(@RequestParam(required = false) String withItems, @PathVariable(value = "userId") Long userId) {
        boolean flag = false;
        if(withItems!=null && withItems.equalsIgnoreCase("true")){
            flag = true;
        }
        List<OrderDTO> orderDTOList = orderService.getAllOrdersListByUserID(userId, flag);
        return ResponseEntity.status(200).body(orderDTOList);
    }

    @Operation(summary = "Get  Orders by Order ID from Redis/Cache")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Single Order Retrieved Successfully"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @GetMapping("/{orderID}")
    public ResponseEntity<OrderDTO> getOrderDetailsByOrderID(@RequestParam(required = false) String withItems, @PathVariable(value = "orderID") Long orderID) {
        boolean flag = false;
        if(withItems!=null  && withItems.equalsIgnoreCase("true")){
            flag = true;
        }
       OrderDTO orderDTO = orderService.getOrderDetailsByOrderID(orderID,flag);
        return  ResponseEntity.ok(orderDTO);
    }

    @Operation(summary = "Update Order Status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status Changed Successfully"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @PutMapping("/update-status")
    public ResponseEntity<ResponseDTO> changeOrderStatus(@RequestBody ChangeOrderStatusRec orderStatusRec){
        ResponseDTO responseDTO  = orderService.changeOrderStatus(orderStatusRec);
        return ResponseEntity.ok(responseDTO);
    }

    @Hidden
    @Operation(summary = "Get  Orders  by Order ID From Redis/Cache")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Single Order Retrieved Successfully"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @GetMapping("/cache/{orderID}")
    public ResponseEntity<OrderDTO> getOrderFromCache(@PathVariable(value = "orderID") Long orderID) {
        OrderDTO  cachedOrder =  orderService.getOrderFromCache(orderID);
        return  ResponseEntity.ok(cachedOrder);
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
