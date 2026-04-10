package com.kmicro.product.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "products")
@Getter @Setter
public class ProductEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_schema.product_seq")
    @SequenceGenerator(name = "product_schema.product_seq", allocationSize = 50)
    private Long  Id;

    private String name;
    @Column(columnDefinition = "text")
    private String description;

    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "in_stock")
    private Boolean inStock;

    private Double rating;
    @Column(name = "review_count")
    private Integer reviewCount;
    private String  category;

    @Column(name = "url")
    private String image;
}
