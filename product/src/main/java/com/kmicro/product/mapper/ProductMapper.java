package com.kmicro.product.mapper;

import com.kmicro.product.dtos.ProductDTO;
import com.kmicro.product.entities.ProductEntity;
import org.springframework.data.domain.Slice;

import java.util.List;


public class ProductMapper {

    public  static  List<ProductDTO> mapEntityToDtoList(List<ProductEntity> productEntities){
//       ModelMapper modelMapper = new ModelMapper();
//        List<ProductDTO> productDTOS = new ArrayList<>();
//        for (ProductEntity product : productEntities) {
//            productDTOS.add(modelMapper.map(product, ProductDTO.class));
//        }
//        return productDTOS;
        return productEntities.stream().map(ProductMapper::EntityToDTO).toList();
    }

    public  static  List<ProductDTO> mapEntityToDtoList(Slice<ProductEntity> productEntities){
        return productEntities.stream().map(ProductMapper::EntityToDTO).toList();
    }

    public   static  List<ProductEntity> mapDTOToProductEntity(List<ProductDTO> productDTOS){
        return productDTOS.stream().map(ProductMapper::dtoToEntity).toList();
    }

    public   static  List<ProductEntity> mapDTOToProductEntityNew(List<ProductDTO> productDTOS){
        return productDTOS.stream().map(ProductMapper::dtoToEntityNew).toList();
    }

    public   static  ProductEntity dtoToEntity(ProductDTO productDTO){
        ProductEntity productEntity = new ProductEntity();
        if(null != productDTO.getId()){
            productEntity.setId(productDTO.getId());
        }
        productEntity.setName(productDTO.getName());
        productEntity.setCategory(productDTO.getCategory());
        productEntity.setPrice(productDTO.getPrice());
        productEntity.setStockQuantity(productDTO.getQuantity());
        productEntity.setImage(productDTO.getImage());
        productEntity.setRating(productDTO.getRating());
        productEntity.setDescription(productDTO.getDescription());
        productEntity.setReviewCount(productDTO.getReviewCount());
        productEntity.setInStock(productDTO.getInStock());
        productEntity.setIsActive(productDTO.getIsActive());
        return productEntity;
    }

    public   static  ProductEntity dtoToEntityNew(ProductDTO productDTO){
        ProductEntity productEntity = new ProductEntity();
//        if(null != productDTO.getId()){
//            productEntity.setId(productDTO.getId());
//        }
        productEntity.setName(productDTO.getName());
        productEntity.setCategory(productDTO.getCategory());
        productEntity.setPrice(productDTO.getPrice());
        productEntity.setStockQuantity(productDTO.getQuantity());
        productEntity.setImage(productDTO.getImage());
        productEntity.setRating(productDTO.getRating());
        productEntity.setDescription(productDTO.getDescription());
        productEntity.setReviewCount(productDTO.getReviewCount());
        productEntity.setInStock(productDTO.getInStock());
        productEntity.setIsActive(productDTO.getIsActive());
        return productEntity;
    }

    public   static  ProductDTO EntityToDTO(ProductEntity product){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCategory(product.getCategory());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getStockQuantity());
        productDTO.setImage(product.getImage());
        productDTO.setDescription(product.getDescription());
        productDTO.setRating(product.getRating());
        productDTO.setReviewCount(product.getReviewCount());
        productDTO.setInStock(product.getInStock());
        productDTO.setIsActive(product.getIsActive());
        return productDTO;
    }


    public static void updateEntityFromDto(ProductDTO dto, ProductEntity entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setStockQuantity(dto.getQuantity());
        entity.setPrice(dto.getPrice());
        entity.setStockQuantity(dto.getQuantity());
        entity.setImage(dto.getImage());
        entity.setDescription(dto.getDescription());
        entity.setReviewCount(dto.getReviewCount());
        entity.setInStock(dto.getInStock());
        entity.setRating(dto.getRating());
        entity.setIsActive(dto.getIsActive());
    }
}// endClass
