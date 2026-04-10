package com.kmicro.order.repository;

import com.kmicro.order.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository  extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Long userId);
    Optional<OrderEntity>findByIdAndUserId(Long Id,Long userId);
   Boolean existsByUserId(Long userId);
}
