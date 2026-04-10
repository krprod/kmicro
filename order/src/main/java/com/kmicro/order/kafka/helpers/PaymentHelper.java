package com.kmicro.order.kafka.helpers;

import com.kmicro.order.dtos.ProcessPaymentRecord;
import com.kmicro.order.entities.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentHelper {

    public PaymentEntity getNewPaymentEntity(ProcessPaymentRecord paymentRecord){
        return PaymentEntity.builder()
                .method(paymentRecord.method())
                .orderId(paymentRecord.orderId())
                .userId(paymentRecord.userID())
                .totalAmount(paymentRecord.amount())
                .shippingFee(paymentRecord.shipping_fee())
                .transactionId(this.getTransactionID())
                .build();
    }

    public  String getTransactionID(){
        return "pay_" + this.paymentID(14);
    }

    private  String paymentID(Integer length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }
}
