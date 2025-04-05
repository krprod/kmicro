package com.kmicro.product.controller;

import com.kmicro.product.dtos.ProductDTO;
import com.kmicro.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

        @Autowired
        ProductService productService;

        @GetMapping
        public ResponseEntity<List<ProductDTO>>getProducts(){
            List<ProductDTO> productsList =  productService.getAllProducts();
            return ResponseEntity.status(200).body(productsList);
        }

       @GetMapping(value = "/{id}")
        public ResponseEntity<ProductDTO> getProductsById(@PathVariable(name = "id") Long id){
           ProductDTO  product = productService.getProductById(id);
            return ResponseEntity.status(200).body(product);
        }


        @GetMapping(value = "/seg")
    public ResponseEntity<List<ProductDTO>> sortAndFilterProduct(@RequestParam(value = "sort", required = false) String sortBy,
                                                                 @RequestParam(value = "filter", required = false) String filterOn){
            List<ProductDTO> productDTOList = null;
            if(null != sortBy){
                System.out.println(sortBy);
            }
            if(null != filterOn){
                System.out.println(filterOn);
            }
            return  ResponseEntity.status(200).body(productDTOList);
        }








}//endClass
