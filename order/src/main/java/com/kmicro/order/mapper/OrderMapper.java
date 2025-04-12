package com.kmicro.order.mapper;

import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.entities.OrderEntity;

import java.util.List;

public class OrderMapper {

 /*   public static OrderEntity mapDTOToEntity(OrderDTO orderDTO) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderDTO.getId());
        orderEntity.setUserId(orderDTO.getUserId());
        orderEntity.setOrderDate(orderDTO.getOrderDate());
        orderEntity.setOrderStatus(orderDTO.getOrderStatus());
        orderEntity.setOrderTotal(orderDTO.getOrderTotal());
        orderEntity.setPaymentMethod(orderDTO.getPaymentMethod());
        orderEntity.setTransactionId(orderDTO.getTransactionId());
        orderEntity.setTrackingNumber(orderDTO.getTrackingNumber());
        return orderEntity;
    }*/

    public static List<OrderDTO> mapEntityListToDTOList(List<OrderEntity> orderEntities) {
        return orderEntities.stream().map(OrderMapper::mapEntityToDTOWithoutItems).toList();
    }

    public static  OrderDTO mapEntityToDTOWithoutItems(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setUserId(orderEntity.getUserId());
        orderDTO.setOrderDate(orderEntity.getOrderDate());
        orderDTO.setOrderStatus(orderEntity.getOrderStatus().name());
        orderDTO.setOrderTotal(orderEntity.getOrderTotal());
        orderDTO.setPaymentMethod(orderEntity.getPaymentMethod().name());
        orderDTO.setPaymentStatus(orderEntity.getPaymentStatus());
        orderDTO.setTransactionId(orderEntity.getTransactionId());
        orderDTO.setShippingFee(orderEntity.getShippingFee());
        orderDTO.setTrackingNumber(orderEntity.getTrackingNumber());
//        orderDTO.setOrderItems(OrderItemMapper.mapEntityListToDTOList(orderEntity.getOrderItems()));
        return orderDTO;
    }

    public static  OrderDTO mapEntityToDTOWithItems(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setUserId(orderEntity.getUserId());
        orderDTO.setOrderDate(orderEntity.getOrderDate());
        orderDTO.setOrderStatus(orderEntity.getOrderStatus().name());
        orderDTO.setOrderTotal(orderEntity.getOrderTotal());
        orderDTO.setPaymentMethod(orderEntity.getPaymentMethod().name());
        orderDTO.setTransactionId(orderEntity.getTransactionId());
        orderDTO.setPaymentStatus(orderEntity.getPaymentStatus());
        orderDTO.setShippingFee(orderEntity.getShippingFee());
        orderDTO.setTrackingNumber(orderEntity.getTrackingNumber());
        orderDTO.setOrderItems(OrderItemMapper.mapEntityListToDTOList(orderEntity.getOrderItems()));
        return orderDTO;
    }
}
