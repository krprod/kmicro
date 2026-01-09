package com.kmicro.notification.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class Order {
    private  Long Id;

    @JsonProperty( "user_id")
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SS")
    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty( "order_total")
    private Double orderTotal;

    @JsonProperty("payment_method")
    private  String paymentMethod;

    @JsonProperty("transaction_id")
    private  String transactionId;

    @JsonProperty("payment_status")
    private  String paymentStatus;

    @JsonProperty("shipping_fee")
    private  String shippingFee;

    @JsonProperty("shipping_address")
    private OrderAddress shippingAddress;

    @JsonProperty( "tracking_number")
    private  String trackingNumber;

    private List<OrderItem> orderItems;
//
//    private String orderNumber;
//    private LocalDate orderDate;
//    private Customer customer;
//    private List<OrderItem> orderItems;
//    private double subtotal;
//    private String shippingCostDescription; // Could also be a double if you have a fixed cost
//    private String paymentMethod;
//    private double totalAmount;
//    private double igstAmount; // Example for tax
//    private Address billingAddress;
//    private Address shippingAddress;

}
