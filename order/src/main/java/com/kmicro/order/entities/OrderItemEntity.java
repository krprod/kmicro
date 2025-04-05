package com.kmicro.order.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

 /*   @ManyToOne
    @JoinColumn(name = "order_id")
    @Column(name = "order_id")
    private Long orderId;*/
 @ManyToOne
 @JoinColumn(name = "order_id")
 private OrderEntity order;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "unit_price")
    private Double price;

    private Integer quantity;

    /*    @Column(name = "user_id")
    private  Long userId;*/

    // can also add subTotal, discountAmount, taxAmount, notesFromUser
}
