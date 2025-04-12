package com.kmicro.notification.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class Order {

    private String orderNumber;
    private LocalDate orderDate;
    private Customer customer;
    private List<OrderItem> orderItems;
    private double subtotal;
    private String shippingCostDescription; // Could also be a double if you have a fixed cost
    private String paymentMethod;
    private double totalAmount;
    private double igstAmount; // Example for tax
    private Address billingAddress;
    private Address shippingAddress;

}
