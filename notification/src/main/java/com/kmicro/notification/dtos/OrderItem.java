package com.kmicro.notification.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Long id;
    private Integer quantity;
    private Double price;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("item_img")
    private String itemImg;
    @JsonProperty("item_name")
    public String itemName;
    /* private String name;
    private int quantity;
    private double price;*/
}
