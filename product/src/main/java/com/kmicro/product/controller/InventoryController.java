package com.kmicro.product.controller;

import com.kmicro.product.dtos.CategoryDTO;
import com.kmicro.product.dtos.ProductDTO;
import com.kmicro.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
      ProductService productService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/check-db")
    public String checkDatabaseConnection() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            return "Database connection successful. Result: " + result;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }


    @PostMapping(value = "/add-update")
    public ResponseEntity<String> addUpdateProduct(@RequestBody List<ProductDTO> productList) {
        Boolean result = productService.addUpdateProduct(productList);
        return result ?ResponseEntity.ok("Product added") : ResponseEntity.badRequest().body("Product not added");
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "id") Long id){
         productService.deleteProduct(id);
        return  ResponseEntity.status(200).body("Deleted");
    }

    @PostMapping(value = "/category")
    public  ResponseEntity<String>addCategory(@RequestBody CategoryDTO categoryDTO){
        productService.addCategory(categoryDTO);
        return ResponseEntity.status(200).body("Category Created.");
    }
    @PutMapping(value = "/category")
    public  ResponseEntity<String>updateCategory(@RequestBody CategoryDTO categoryDTO){
        Boolean stat = productService.updateCategory(categoryDTO);
        return  stat ? ResponseEntity.status(200).body("Category Updated.") : ResponseEntity.status(200).body("record not found") ;
    }

    @DeleteMapping(value = "/category/{catID}")
    public  ResponseEntity<String>deleteCategory(@PathVariable(name = "catID") Long catId ){
        Boolean result = productService.removeCategory(catId);
        return result ? ResponseEntity.status(200).body("Category Deleted.") : ResponseEntity.status(400).body("cannot deleteCategory, Mapped with products") ;
    }



}// endClass
