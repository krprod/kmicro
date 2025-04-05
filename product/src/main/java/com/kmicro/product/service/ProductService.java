package com.kmicro.product.service;

import com.kmicro.product.dtos.CategoryDTO;
import com.kmicro.product.dtos.ProductDTO;
import com.kmicro.product.entities.CategoryEntity;
import com.kmicro.product.entities.ProductEntity;
import com.kmicro.product.mapper.CategoryMapper;
import com.kmicro.product.mapper.ProductMapper;
import com.kmicro.product.repository.CategoryRepository;
import com.kmicro.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;


    public List<ProductDTO> getAllProducts() {
        List<ProductEntity> productEntities = productRepository.findAll();
        return ProductMapper.mapEntityToProductDto(productEntities);

    }



    public Boolean addUpdateProduct(List<ProductDTO> product) {
        // need to handle agr categoryID exist nahi, to product  add na ho
        List<ProductEntity> productEntityList = ProductMapper.mapDTOToProductEntity(product);
        try {
            List<ProductEntity> savedEntityList = productRepository.saveAll(productEntityList);
            if (savedEntityList.size() > 0) {
                return true;
            }
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return false;
    }


    public void addCategory(CategoryDTO categoryDTO) {
        CategoryEntity category = CategoryMapper.mapDtoToEntity(categoryDTO);
        categoryRepository.save(category);
    }

    public Boolean updateCategory(CategoryDTO categoryDTO) {
        CategoryEntity category = CategoryMapper.mapDtoToEntity(categoryDTO);
        Optional exists = categoryRepository.findById(category.getId());
        if(exists.isPresent()){
            categoryRepository.save(category);
            return  true;
        }
        return false;
    }

    public Boolean removeCategory(Long catId) {
        List<ProductEntity> productEntityList =  productRepository.findByCategoryID(catId);
        if(productEntityList.size() >= 1 ){
            return  false;
        }
        categoryRepository.deleteById(catId);
        return  true;
    }

    public void deleteProduct(Long id) {
        // check carts cache before delete
        // only admin can final delete
        productRepository.deleteById(id);
    }

    public ProductDTO getProductById(Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        if(product.isPresent()){
             return  ProductMapper.EntityToDTO(product.get());
        }
        return new  ProductDTO();
    }
}// endClass
