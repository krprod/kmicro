package com.kmicro.order.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.constants.AppConstants;
import com.kmicro.order.dtos.CheckoutDetailsDTO;
import com.kmicro.order.dtos.OrderDTO;
import com.kmicro.order.entities.OrderEntity;
import com.kmicro.order.utils.DateUtil;
import com.kmicro.order.utils.DynamicEventCreator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        orderDTO.setOrderStatus(orderEntity.getStatus().name());
        orderDTO.setSubtotal(orderEntity.getSubtotal());
        orderDTO.setTotalAmount(orderEntity.getTotalAmount());
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
            orderdto.setShippingAddress(mapper.readValue(orderEntity.getShippingAddress(), CheckoutDetailsDTO.class));
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
        orderDTO.setOrderStatus(orderEntity.getStatus().name());
        orderDTO.setSubtotal(orderEntity.getSubtotal());
        orderDTO.setTotalAmount(orderEntity.getTotalAmount());
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
            orderdto.setShippingAddress(mapper.readValue(orderEntity.getShippingAddress(), CheckoutDetailsDTO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return orderdto;
    }

    public static Map<String, Object> mapDynmicFieldOrderConfirmation(OrderEntity order, ObjectMapper mapper){
        DynamicEventCreator dynamicEventCreator = new DynamicEventCreator();
        DynamicEventCreator body = new DynamicEventCreator();
//        DynamicEventCreator item = new DynamicEventCreator();
        DynamicEventCreator totaling = new DynamicEventCreator();
        DynamicEventCreator details = new DynamicEventCreator();

        dynamicEventCreator.add("sendto","TEMPA-"+ order.getUserId());
        dynamicEventCreator.add("subject","Order Confirmed");

        body.add("title","Order Confirmed");
        body.add("greetingByName","Hi, TEMPA-"+ order.getUserId()+",  your order is confirmed!");
        body.add("msgLine1","Thank you for shopping with us. Your order details are below.");
        body.add("trackingUrl", AppConstants.TRACKING_URL + order.getTrackingNumber());

        //------ OrderItems Object
        List<DynamicEventCreator> listOfItems =  order.getOrderItems().stream()
                .map(item->
                        new DynamicEventCreator()
                                .addFluently("id", item.getId())
                                .addFluently("quantity", item.getQuantity())
                                .addFluently("price", item.getPrice())
                                .addFluently("product_id", item.getProductId())
                                .addFluently("img",AppConstants.PRODUCT_URL + item.getItemImg())
                                .addFluently("link", AppConstants.PRODUCT_URL + item.getProductId())
                                .addFluently("name", item.getItemName())
                ).collect(Collectors.toList());

     /*   List<DynamicEventCreator> listOfItems = new ArrayList<>();
        for(OrderItemEntity orderItem : order.getOrderItems()){
            item.clean();

            item.add("id", orderItem.getId());
            item.add("quantity", orderItem.getQuantity());
            item.add("price", orderItem.getPrice());
            item.add("product_id", orderItem.getProductId());
            item.add("img",AppConstants.PRODUCT_URL + orderItem.getItemImg());
            item.add("link", AppConstants.PRODUCT_URL + orderItem.getProductId());
            item.add("name", orderItem.getItemName());

            listOfItems.add(item);

        }*/

        body.add("items",listOfItems);
        //------ User Details Object
        try {
            var shippingDetails = mapper.readValue(order.getShippingAddress(), CheckoutDetailsDTO.class);
            details.addFluently("name", "TEMPA")
                    .addFluently("contact", "TEMPA")
                    .addFluently("email", "TEMPA")
                    .addFluently("country", shippingDetails.getCountry())
                    .addFluently("city", shippingDetails.getCity())
                    .addFluently("address_id", shippingDetails.getAddressID())
                    .addFluently("shipping_address", shippingDetails.getStreet())
                    .addFluently("zip_code", shippingDetails.getZipCode());
  /* details.add("country", shippingDetails.get("country"));
            details.add("city", shippingDetails.get("city"));
            details.add("address_id", shippingDetails.get("address_id"));
            details.add("shipping_address", shippingDetails.get("shipping_address"));
            details.add("zip_code", shippingDetails.get("zip_code"));*/
            body.add("details",details);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //------ Totaling Object
        totaling
                .addFluently("totalPrice",order.getTotalAmount())
                .addFluently("subtotal",order.getSubtotal())
                .addFluently("shippingFee",order.getShippingFee());

        body.add("totaling",totaling);

        dynamicEventCreator.add("body",body);
        return dynamicEventCreator.getPayload();
    }

    public static Map<String, Object> mapDynmicFieldOrderStatusUpdate(OrderEntity order, ObjectMapper mapper){
        DynamicEventCreator dynamicEventCreator = new DynamicEventCreator();
        DynamicEventCreator body = new DynamicEventCreator();

        dynamicEventCreator.add("sendto","TEMPA-"+ order.getUserId());
        dynamicEventCreator.add("subject","Order Status Update");

        body.add("title","Order Status Update");
        body.add("statusTitle","Your Order Status is updated to "+order.getStatus().name());
        body.add("statusMessage","You can get lastest tracking updates for your package using below link and trackingNumber");
        body.add("trackingNumber",order.getTrackingNumber());
        body.add("carrierName","FedEx");
        body.add("trackingUrl", AppConstants.TRACKING_URL + order.getTrackingNumber());

        dynamicEventCreator.add("body",body);
        return dynamicEventCreator.getPayload();
    }
}
