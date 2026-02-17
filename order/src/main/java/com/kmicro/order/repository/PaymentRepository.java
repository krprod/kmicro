package com.kmicro.order.repository;

import com.kmicro.order.entities.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

}
