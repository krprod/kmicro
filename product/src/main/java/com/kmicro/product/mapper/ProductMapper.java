package com.kmicro.product.mapper;

import com.kmicro.product.dtos.ProductDTO;
import com.kmicro.product.entities.ProductEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;


public class ProductMapper {

    public  static  List<ProductDTO> mapEntityToProductDto(List<ProductEntity> productEntities){
       ModelMapper modelMapper = new ModelMapper();
        List<ProductDTO> productDTOS = new ArrayList<>();
        for (ProductEntity product : productEntities) {
            productDTOS.add(modelMapper.map(product, ProductDTO.class));
        }
        return productDTOS;
    }

    public   static  List<ProductEntity> mapDTOToProductEntity(List<ProductDTO> productDTOS){
       ModelMapper modelMapper = new ModelMapper();
        List<ProductEntity> productEntities = new ArrayList<>();
        for (ProductDTO product : productDTOS) {
                productEntities.add( ProductMapper.dtoToEntity(product));
//            productEntities.add(
//                    modelMapper.typeMap( ProductDTO.class,ProductEntity.class)
//                            .addMappings(mapper->{
//                                mapper.map(productDTO -> productDTO.getQuantity(), ProductEntity::setStockQuantity);
//                            })
//            );

                    //product, ProductEntity.class
        }
        return productEntities;
    }

    public   static  ProductEntity dtoToEntity(ProductDTO productDTO){
        ProductEntity productEntity = new ProductEntity();
        if(null != productDTO.getId()){
            productEntity.setId(productDTO.getId());
        }
        productEntity.setName(productDTO.getName());
        productEntity.setCategoryID(productDTO.getCategoryID());
        productEntity.setPrice(productDTO.getPrice());
        productEntity.setStockQuantity(productDTO.getQuantity());
        productEntity.setImage(productDTO.getImage());
        return productEntity;
    }

    public   static  ProductDTO EntityToDTO(ProductEntity product){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCategoryID(product.getCategoryID());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getStockQuantity());
        productDTO.setImage(product.getImage());
        return productDTO;
    }




}// endClass
