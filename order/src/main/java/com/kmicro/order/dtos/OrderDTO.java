package com.kmicro.order.dtos;

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

    private  Long Id;

    @JsonProperty( "user_id")
    private Long userId;

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

    @JsonProperty( "tracking_number")
    private  String trackingNumber;

    private List<OrderItemDTO> orderItems;
}
