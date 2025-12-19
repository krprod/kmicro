package com.kmicro.product.dtos;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProductDTO {

    private Long  id;
    @NonNull
    private String name;
    @NonNull
    private Double price;
    @NonNull
    private Integer quantity;
    @NonNull
    private Integer  categoryID;

    private String image;
}
