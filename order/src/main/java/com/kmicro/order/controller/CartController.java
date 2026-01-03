package com.kmicro.order.controller;

import com.kmicro.order.dtos.CartDTO;
import com.kmicro.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@Tag(name = "Cart Controller", description = "Operations for cart lifecycle")
public class CartController {

    @Autowired
    CartService cartService;

    @Operation(summary = "Get Cart By User Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Complete Cart By User ID"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @GetMapping("/{userID}")
    public ResponseEntity<List<CartDTO>> getCartByUserID(@PathVariable(value = "userID") String userId){
        List<CartDTO> productList = cartService.getCartByUserID(userId);
        return ResponseEntity.status(200).body(productList);
    }

    @Operation(summary = "Add-Update Cart Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Add-Update Cart Succeed"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @PutMapping("/add-update")
    public ResponseEntity<Void> addUpdateCart(@RequestBody CartDTO cartDTO){
        cartService.addUpdateCart(cartDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove Cart Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart Item Removed Successfully"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @PutMapping("/remove")
    public  ResponseEntity<Void>removeProductFromCart(@RequestBody CartDTO cartDTO){
        cartService.removeItemFromCart(cartDTO);
       return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove Cart By UserID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Succeed"),
            @ApiResponse(responseCode = "400", description = "Failed Global Handler")
    })
    @DeleteMapping("/remove/{userID}")
    public  ResponseEntity<Void>deleteCart(@PathVariable(value = "userID") String userID){
         cartService.deleteCart(userID);
        return ResponseEntity.noContent().build();
    }
}
