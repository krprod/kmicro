package com.kmicro.product.repository;

import com.kmicro.product.entities.ProductEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;


public class ProductSpecifications {
    public static Specification<ProductEntity> withFilters(String category, Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            predicates.add(cb.greaterThan(root.get("stockQuantity"), 1));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProductEntity> searchByKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            // Multi-column search: name OR description OR brand
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern)
//                    cb.like(cb.lower(root.get("description")), pattern),
//                    cb.like(cb.lower(root.get("brand")), pattern)
            );
        };
    }

    public static Specification<ProductEntity> applySearchAndFilter(String category, Double minPrice, Double maxPrice, String keyword){
        Specification<ProductEntity> spec = Specification.where(null);
        Specification<ProductEntity> filterSpecs = ProductSpecifications.withFilters(category, minPrice, maxPrice);
        Specification<ProductEntity> searchSpec = ProductSpecifications.searchByKeyword(keyword);

        return spec.and(searchSpec).and(filterSpecs);
    }
}
