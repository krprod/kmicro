package com.kmicro.product.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "categories")
@Getter @Setter
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private long Id;

    private  String name;

    @Column(unique = true)
    private  String slug;

    private boolean is_active = true;

    private LocalDateTime created_at = LocalDateTime.now();

}
