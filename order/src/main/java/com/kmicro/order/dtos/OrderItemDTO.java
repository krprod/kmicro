package com.kmicro.order.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDTO {
    private Long id;
    private Integer quantity;
    private Double price;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("product_id")
    private Long productId;
}
