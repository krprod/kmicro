package com.kmicro.order.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO {
    @JsonProperty("user_id")
    public Long userId;
    @JsonProperty("product_id")
    public Long productId;
    @JsonProperty("product_name")
    public String productName;
    public String img;
    public Integer quantity;
    public Double price;
}
