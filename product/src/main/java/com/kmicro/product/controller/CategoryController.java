package com.kmicro.product.controller;

import com.kmicro.product.dtos.CategoryDTO;
import com.kmicro.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@Tag(name = "Category Controller", description = "Operations for category lifecycle")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Operation(summary = "List All Categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully processed all data"),
            @ApiResponse(responseCode = "400", description = "Failed to process data handled globalError Handler")
    })
    @GetMapping
    public ResponseEntity<List<CategoryDTO>>getAllCategoryList(){
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.status(200).body(categories);
    }

    @Operation(summary = "Add New Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully processed all data"),
            @ApiResponse(responseCode = "400", description = "Failed to process data handled globalError Handler")
    })
    @PostMapping
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO categoryDTO){
       CategoryDTO category = categoryService.addCategory(categoryDTO);
        return ResponseEntity.status(200).body(category);
    }

    @Operation(summary = "Update ExistingCategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully processed all data"),
            @ApiResponse(responseCode = "400", description = "Failed to process data handled globalError Handler")
    })
    @PutMapping
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO categoryUpdated = categoryService.updateCategory(categoryDTO);
        return ResponseEntity.status(200).body(categoryUpdated);
    }

//    @DeleteMapping(value = "/category/{catID}")
//    public  ResponseEntity<String>deleteCategory(@PathVariable(name = "catID") Long catId ){
//        Boolean result = productService.removeCategory(catId);
//        return result ? ResponseEntity.status(200).body("Category Deleted.") : ResponseEntity.status(400).body("cannot deleteCategory, Mapped with products") ;
//    }



}// endClass
