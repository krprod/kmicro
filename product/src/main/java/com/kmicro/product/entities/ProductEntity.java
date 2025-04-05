package com.kmicro.product.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "products")
@Getter @Setter
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long  Id;

    private String name;

    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    private Integer  categoryID;

    @Column(name = "url")
    private String image;
}
