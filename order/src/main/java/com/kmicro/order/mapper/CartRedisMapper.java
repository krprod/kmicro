package com.kmicro.order.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.order.dtos.CartDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartRedisMapper {



    public static List<CartDTO> getListOfProducts(Map<Object, Object> cachedCart) {
        return cachedCart.values()
                .stream()
                .map(obj -> (CartDTO) obj) // Cast to CartDTO
                .collect(Collectors.toList());
//        return cartDTOs;
    }

    public static List<CartDTO> getListOfProducts(List<Object> cachedCart, ObjectMapper objectMapper) {
        return cachedCart
                .stream()
                .map(obj -> objectMapper.convertValue(obj, CartDTO.class)) // Cast to CartDTO
                .collect(Collectors.toList());
    }

}//EC