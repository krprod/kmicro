package com.kmicro.order.entities;

import com.kmicro.order.constants.Status;
import com.kmicro.order.constants.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderEntity extends BaseEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_schema.orders_seq")
    @SequenceGenerator(name = "order_schema.orders_seq", allocationSize = 50)
     private  Long Id;

    @Column(name = "user_id")
    private Long userId;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SS")
    @Column(name = "order_date")
    private Instant orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private Status status;

    @Column(name = "subtotal")
    private Double subtotal;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id")
    private  String transactionId;

    @Column(name = "payment_status")
    private  String paymentStatus;

    @Column(name = "shipping_fee")
    private  Double shippingFee;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private  String shippingAddress;

    @Column(name = "tracking_number")
    private  String trackingNumber;



    @OneToMany(mappedBy = "order", targetEntity = OrderItemEntity.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY,   orphanRemoval = true)
    private List<OrderItemEntity> orderItems;

    public void setInitialTimeStamp( Instant currentTime){
        this.orderDate = currentTime;
        this.setCreatedAt(currentTime);
        this.setUpdatedAt(currentTime);
    }

    //can add estimate delivery date, actual delivery date, shipping address,   shippingMethod,

}
