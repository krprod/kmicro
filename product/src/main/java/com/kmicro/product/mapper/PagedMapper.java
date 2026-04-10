package com.kmicro.product.mapper;

import com.kmicro.product.dtos.PagedResponseDTO;
import org.springframework.data.domain.Page;

public class PagedMapper {
    public static <T> PagedResponseDTO<T> toPagedResponse(Page<T> page) {
        return PagedResponseDTO.<T>builder()
                .content(page.getContent())
                .metadata(PagedResponseDTO.Metadata.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .isLast(page.isLast())
                        .numberOfElements(page.getNumberOfElements())
                        .build())
                .build();
    }
}
