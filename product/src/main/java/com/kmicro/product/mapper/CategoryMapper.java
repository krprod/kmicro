package com.kmicro.product.mapper;

import com.kmicro.product.dtos.CategoryDTO;
import com.kmicro.product.entities.CategoryEntity;
import org.modelmapper.ModelMapper;

public class CategoryMapper {

    public static CategoryEntity mapDtoToEntity(CategoryDTO categoryDTO){
            ModelMapper modelMapper = new ModelMapper();
           return   modelMapper.map(categoryDTO, CategoryEntity.class);
    }

    public static CategoryDTO  mapEntityToDTO(CategoryEntity entity){
            ModelMapper modelMapper = new ModelMapper();
           return   modelMapper.map(entity, CategoryDTO.class);
    }



}//end class
