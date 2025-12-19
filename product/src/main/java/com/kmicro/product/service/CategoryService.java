package com.kmicro.product.service;

import com.kmicro.product.dtos.CategoryDTO;
import com.kmicro.product.entities.CategoryEntity;
import com.kmicro.product.exception.AlreadyExistException;
import com.kmicro.product.exception.DataNotExistException;
import com.kmicro.product.mapper.CategoryMapper;
import com.kmicro.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
            List<CategoryEntity> categoryEntities = categoryRepository.findAll();
            if (categoryEntities.isEmpty()) {
                return Collections.emptyList(); // Never return null
            }
            return CategoryMapper.mapEntityListToDTO(categoryEntities);
    }

    @Transactional
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        categoryDTO.setId(0L); // QUICK FIX
        if(categoryRepository.existsBySlug(categoryDTO.getSlug())){
            throw new AlreadyExistException("Category with slug: " + categoryDTO.getSlug() + " already exists");
        }
        CategoryEntity category = CategoryMapper.mapDtoToEntity(categoryDTO);

        CategoryEntity savedCategory = categoryRepository.save(category);

        return CategoryMapper.mapEntityToDTO(savedCategory);
    }

    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        // 1. Validate ID exists first
        CategoryEntity existingCategory = categoryRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new DataNotExistException("Category not found with id: " + categoryDTO.getId()));

        // 2. Check for slug conflict ONLY if the slug is actually changing
        if (!existingCategory.getSlug().equals(categoryDTO.getSlug())) {
            if (categoryRepository.existsBySlug(categoryDTO.getSlug())) {
                throw new AlreadyExistException("Slug already in use: " + categoryDTO.getSlug());
            }
        }

        // 3. Perform the update on the managed entity
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setSlug(categoryDTO.getSlug());
        existingCategory.set_active(categoryDTO.is_active());

        // No return needed if using exceptions for flow control,
        // or return the mapped DTO for the frontend.
        return CategoryMapper.mapEntityToDTO(existingCategory);
    }

}//EC
