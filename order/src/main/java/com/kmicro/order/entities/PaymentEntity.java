package com.kmicro.order.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_schema.payment_seq")
    @SequenceGenerator(name = "order_schema.payment_seq", allocationSize = 50)
    private  Long Id;

    @Column(name ="order_id")
    private Long orderId;

    @Column(name ="total_amount")
    private Double totalAmount;

    @Column(name ="payment_method")
    private String method;

    @Column(name ="payment_status")
    private String status;

    @Column(name ="fail_reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name ="transaction_id")
    private String transactionId;

    @Column(name ="user_id")
    private Long userId;

    @Column(name ="shipping_fee")
    private Double shippingFee;

}
