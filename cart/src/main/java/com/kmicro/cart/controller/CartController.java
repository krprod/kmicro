package com.kmicro.cart.controller;

import com.kmicro.cart.dtos.CartDTO;
import com.kmicro.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart/{userID}")
    public ResponseEntity<  List<CartDTO>> getAllCarts(@PathVariable(value = "userID") String user_id){
        List<CartDTO> productList = cartService.getCartDetails(user_id);
        return ResponseEntity.status(200).body(productList);
    }

    @PostMapping("/cart/add-update")
    public ResponseEntity<String> addUpdateCart(@RequestBody CartDTO cartDTO){
        cartService.addUpdateCart(cartDTO);
        return ResponseEntity.status(200).body("Added");
    }

    @DeleteMapping("/cart/remove")
    public  ResponseEntity<String>removeFromCart(@RequestBody CartDTO cartDTO){
       Long  status = cartService.removeFromCart(cartDTO);
        return  status == 1 ? ResponseEntity.ok("Removed") : ResponseEntity.status(400).body("Not Removed");
    }

}
