package com.kmicro.cart.mapper;

import com.kmicro.cart.dtos.CartDTO;
import io.lettuce.core.resource.DnsResolver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartRedisMapper {


    public static List<CartDTO> getListOfProducts(Map<Object, Object> cachedCart) {
        List<CartDTO> cartDTOs = cachedCart.values()
                .stream()
                .map(obj -> (CartDTO) obj) // Cast to CartDTO
                .collect(Collectors.toList());
        return cartDTOs;
    }
    


}// end class
