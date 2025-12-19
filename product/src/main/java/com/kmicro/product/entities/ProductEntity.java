package com.kmicro.product.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "products")
@Getter @Setter
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  Id;

    private String name;

    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    private Integer  categoryID;

    @Column(name = "url")
    private String image;
}
