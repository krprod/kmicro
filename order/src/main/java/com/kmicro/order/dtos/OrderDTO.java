package com.kmicro.order.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO  implements Serializable {
    private static final long serialVersionUID = 1L;
    private  Long Id;

    @JsonProperty( "user_id")
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SS")
    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty( "subtotal")
    private Double subtotal;

    @JsonProperty("total_amount")
    private Double totalAmount;

    @JsonProperty("payment_method")
    private  String paymentMethod;

    @JsonProperty("transaction_id")
    private  String transactionId;

    @JsonProperty("payment_status")
    private  String paymentStatus;

    @JsonProperty("shipping_fee")
    private  Double shippingFee;

    @JsonProperty("shipping_address")
    private OrderAddressDTO shippingAddress;

    @JsonProperty( "tracking_number")
    private  String trackingNumber;

    private List<OrderItemDTO> orderItems;
}
