package com.kmicro.order.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.dtos.OrderAddressDTO;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.utils.DateUtil;

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

    public static List<OrderDTO> entityToDTOListWithoutItems(List<OrderEntity> orderEntities) {
        return orderEntities.stream().map(OrderMapper::mapEntityToDTOWithoutItems).toList();
    }
    public static List<OrderDTO> entityToDTOListWithoutItems(List<OrderEntity> orderEntities, ObjectMapper mapper) {
        return orderEntities.stream().map((e)->OrderMapper.mapEntityToDTOWithoutItems(e, mapper)).toList();
    }

    public static List<OrderDTO> entityToDTOListWithItems(List<OrderEntity> orderEntities) {
        return orderEntities.stream().map(OrderMapper::mapEntityToDTOWithItems).toList();
    }
    public static List<OrderDTO> entityToDTOListWithItems(List<OrderEntity> orderEntities, ObjectMapper mapper) {
        return orderEntities.stream().map((e)->OrderMapper.mapEntityToDTOWithItems(e,mapper)).toList();
    }

    public static  OrderDTO mapEntityToDTOWithoutItems(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setUserId(orderEntity.getUserId());
        orderDTO.setOrderDate(DateUtil.convertInstantToLDT(orderEntity.getOrderDate()));
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
    public static  OrderDTO mapEntityToDTOWithoutItems(OrderEntity orderEntity, ObjectMapper mapper) {
        var orderdto = OrderMapper.mapEntityToDTOWithoutItems(orderEntity);
        try {
            orderdto.setShippingAddress(mapper.readValue(orderEntity.getShippingAddress(), OrderAddressDTO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return orderdto;
    }

    public static  OrderDTO mapEntityToDTOWithItems(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setUserId(orderEntity.getUserId());
        orderDTO.setOrderDate(DateUtil.convertInstantToLDT(orderEntity.getOrderDate()));
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
    public static  OrderDTO mapEntityToDTOWithItems(OrderEntity orderEntity, ObjectMapper mapper){
        var orderdto = OrderMapper.mapEntityToDTOWithItems(orderEntity);
        try {
            orderdto.setShippingAddress(mapper.readValue(orderEntity.getShippingAddress(), OrderAddressDTO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return orderdto;
    }
}
