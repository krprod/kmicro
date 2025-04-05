package com.kmicro.payment.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class OrderResponse {

        @JsonProperty("order_id")
        Long orderID;

        @JsonProperty("transaction_id")
        String transactionID;

        @JsonProperty("payment_status")
        String paymentStatus;

}
