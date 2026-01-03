package com.kmicro.order.dtos;

public record ProcessPaymentRecord(Long orderId, Double amount, String method) {
}
