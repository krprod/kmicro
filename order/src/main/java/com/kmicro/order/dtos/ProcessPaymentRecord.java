package com.kmicro.order.dtos;

public record ProcessPaymentRecord(
        Long orderId,
        Double amount,
        String method,
        Long userID,
        String shipping_fee
//        OrderStatus order_status
        ) { }
