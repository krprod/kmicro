package com.kmicro.product.controller;

import com.kmicro.product.dtos.BoughtProductRecord;
import com.kmicro.product.dtos.BulkUpdateResponseRecord;
import com.kmicro.product.dtos.PagedResponseDTO;
import com.kmicro.product.dtos.ProductDTO;
import com.kmicro.product.service.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Validated
@Tag(name = "Product Controller", description = "Operations for product lifecycle")
public class ProductController {

        @Autowired
        ProductService productService;

        @Operation(summary = "List All Products")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully processed all data"),
                @ApiResponse(responseCode = "400", description = "Failed to process data handled globalError Handler")
        })
        @GetMapping
        public ResponseEntity<List<ProductDTO>>getProducts(){
            List<ProductDTO> productsList =  productService.getAllProducts();
            return ResponseEntity.status(200).body(productsList);
        }

        @Operation(summary = "Get Product By ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully processed all data"),
                @ApiResponse(responseCode = "400", description = "Failed to process data handled globalError Handler")
        })
       @GetMapping(value = "/{id}")
        public ResponseEntity<ProductDTO> getProductById(@PathVariable(name = "id") Long id){
           ProductDTO  product = productService.getProductById(id);
            return ResponseEntity.status(200).body(product);
        }

        @Hidden
        @GetMapping(value = "/exists/{id}")
        public ResponseEntity<Boolean>checkProductAvailability(@PathVariable(name = "id") Long id){
            if(!productService.checkProductAvailability(id)){
                return ResponseEntity.status(200).body(false);
            }
            return ResponseEntity.status(200).body(true);
        }

        @Operation(summary = "Bought Product and Change Quantity ", description = "check bought product and update product quantity ")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "207", description = "Partial success - some items failed"),
                @ApiResponse(responseCode = "200", description = "Successfully processed all data"),
                @ApiResponse(responseCode = "400", description = "Failed to process data handled globalError Handler")
        })
        @PostMapping(value = "/bought-products")
        public ResponseEntity<BulkUpdateResponseRecord>boughtProduct(@RequestBody  @Valid List<BoughtProductRecord> productRecord){
            BulkUpdateResponseRecord response  = productService.changeQtyBoughtProduct(productRecord);
            HttpStatus status = response.errors().isEmpty() ? HttpStatus.OK : HttpStatus.MULTI_STATUS;
            return ResponseEntity.status(status).body(response);
        }

    @Operation(summary = "Paginated Results | search, filter,sort ", description = "search, filter,sort products list returns in paginated manner ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully return all data"),
            @ApiResponse(responseCode = "400", description = "Failed to process data handled globalError Handler")
    })
        @GetMapping(value = "/paginated")
        public ResponseEntity< PagedResponseDTO<ProductDTO> > sortAndFilterProduct(
                @RequestParam(required = false) String category,
                @RequestParam(required = false) Double minPrice,
                @RequestParam(required = false) Double maxPrice,
                @Parameter(description = "Searched Keyword", example = "xproduct")
                @RequestParam(required = false) String keyword,
                @ParameterObject Pageable pageable){

            PagedResponseDTO<ProductDTO> productList  = productService.filterAndSortedProductList(keyword,category, minPrice, maxPrice, pageable);
            return  ResponseEntity.status(200).body(productList);
        }

    @Operation(summary = "Add New Products", description = "add new products list in database ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully return all data"),
            @ApiResponse(responseCode = "400", description = "Failed to process data")
    })
    @PostMapping(value = "/add")
    public ResponseEntity<List<ProductDTO>> addProduct(@RequestBody List<ProductDTO> productList) {
        List<ProductDTO> result = productService.addProduct(productList);
        if(result.isEmpty()){
            ResponseEntity.status(400).body(result);
        }
        return  ResponseEntity.ok(result);
    }

    @Operation(summary = "Update Products | Fail-Fast", description = "Add-Updates multiple products and returns success/error ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully processed all data"),
            @ApiResponse(responseCode = "400", description = "Failed to process data")
    })
    @PutMapping(value = "/update")
    public ResponseEntity<List<ProductDTO>> updateProduct(@RequestBody List<ProductDTO> productList) {
        List<ProductDTO> result = productService.updateProduct(productList);
        if(result.isEmpty()){
            ResponseEntity.status(400).body(result);
        }
        return  ResponseEntity.ok(result);
    }

    @Operation(summary = "Bulk update products | Fail-Safe", description = "Updates multiple products and returns partial success/error details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully processed all updates"),
            @ApiResponse(responseCode = "207", description = "Partial success - some items failed"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping(value = "/bulk-update")
    public ResponseEntity<BulkUpdateResponseRecord> bulkUpdateProduct(@RequestBody List<ProductDTO> productList) {
        BulkUpdateResponseRecord response = productService.bulkUpdateProduct(productList);

        HttpStatus status = response.errors().isEmpty() ? HttpStatus.OK : HttpStatus.MULTI_STATUS;
        return ResponseEntity.status(status).body(response);
    }

    @Hidden
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "id") Long id){
        productService.deleteProduct(id);
        return  ResponseEntity.status(200).body("Deleted");
    }








}//endClass
