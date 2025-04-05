package com.kmicro.cart.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO implements Serializable{

    public Long id;
    @JsonProperty("user_id")
    public Long userId;
    @JsonProperty("product_id")
    public Long productId;

    public Integer quantity;
    public Double price;
}
