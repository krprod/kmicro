package com.kmicro.order.repository;

import com.kmicro.order.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository  extends JpaRepository<OrderItemEntity, Long> {
}
