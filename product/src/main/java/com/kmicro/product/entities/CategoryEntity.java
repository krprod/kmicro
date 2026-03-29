package com.kmicro.product.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "categories")
@Getter @Setter
public class CategoryEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_schema.category_seq")
    @SequenceGenerator(name = "product_schema.category_seq", allocationSize = 50)
    @Column(name = "category_id")
    private long Id;

    private  String name;

    @Column(unique = true)
    private  String slug;

    private boolean is_active = true;

    private Instant created_at;

}
