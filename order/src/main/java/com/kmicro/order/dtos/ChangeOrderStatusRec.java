package com.kmicro.order.dtos;

public record ChangeOrderStatusRec(Long orderID, Long userID,String orderStatus) {
}
