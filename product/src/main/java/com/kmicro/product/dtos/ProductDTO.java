package com.kmicro.product.dtos;

import jakarta.persistence.Column;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProductDTO {

    private Long  id;
    @NonNull
    private String name;

    private String description;
    @NonNull
    private Double price;
    @NonNull
    private Integer quantity;

    @NonNull
    private String  category;

    @Column(name = "in_stock")
    private Boolean inStock;

    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    private String image;
}
