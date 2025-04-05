package com.kmicro.order.mapper;

import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.dtos.OrderItemDTO;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.entities.OrderItemEntity;

import java.util.List;

public class OrderItemMapper {
    public static List<OrderItemEntity> mapDTOListToEntityList(List<OrderItemDTO> orderItemDTOList, OrderEntity orderEntity) {
        List<OrderItemEntity> orderItemEntities = orderItemDTOList.stream()
                .map(orderItemDTO -> mapDTOToEntity(orderItemDTO, orderEntity))
                .toList();
        return orderItemEntities;
    }

    private  static OrderItemEntity mapDTOToEntity(OrderItemDTO orderItemDTO, OrderEntity orderEntity) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setProductId(orderItemDTO.getProductId());
        orderItemEntity.setQuantity(orderItemDTO.getQuantity());
        orderItemEntity.setPrice(orderItemDTO.getPrice());
        orderItemEntity.setOrder(orderEntity);
        return orderItemEntity;
    }


    public static List<OrderItemDTO> mapEntityListToDTOList(List<OrderItemEntity> orderItemEntities) {
        List<OrderItemDTO> orderItemDTOList = orderItemEntities.stream()
                .map(OrderItemMapper::mapEntityToDTO)
                .toList();
        return orderItemDTOList;
    }
    private static OrderItemDTO mapEntityToDTO(OrderItemEntity orderItemEntity) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(orderItemEntity.getId());
        orderItemDTO.setProductId(orderItemEntity.getProductId());
        orderItemDTO.setQuantity(orderItemEntity.getQuantity());
        orderItemDTO.setPrice(orderItemEntity.getPrice());
        return orderItemDTO;
    }

}//EC
