package com.kmicro.order.dtos;

public record EditOrderRec(
        String orderStatus,
        Long userID,
        Long orderID,
        String trackingNumber,
        String paymentStatus,
        Double totalAmount) { }
