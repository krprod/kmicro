package com.kmicro.order.entities;

import com.kmicro.order.dtos.OrderStatusEnum;
import com.kmicro.order.dtos.PaymentMethodEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
     private  Long Id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatusEnum orderStatus ;

    @Column(name = "order_total")
    private Double orderTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethodEnum paymentMethod;

    @Column(name = "transaction_id")
    private  String transactionId;

    @Column(name = "payment_status")
    private  String paymentStatus;

    @Column(name = "tracking_number")
    private  String trackingNumber;

    @OneToMany(mappedBy = "order", targetEntity = OrderItemEntity.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY,   orphanRemoval = true)
    private List<OrderItemEntity> orderItems;

    //can add estimate delivery date, actual delivery date, shipping address,   shippingMethod,

}
