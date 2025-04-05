package com.kmicro.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProductDTO {
    private Long  id;
    private String name;
    private Double price;
    private Integer quantity;
    private Integer  categoryID;
    private String image;
}
