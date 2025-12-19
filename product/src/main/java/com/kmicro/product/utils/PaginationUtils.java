package com.kmicro.product.utils;

import com.kmicro.product.dtos.PagedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PaginationUtils {
    private static final int MAX_PAGE_SIZE = 50;

    public static <E, D> PagedResponseDTO<D> toPagedResponse(Page<E> page, List<D> dtos) {
        return PagedResponseDTO.<D>builder()
                .content(dtos)
                .metadata(PagedResponseDTO.Metadata.builder()
                        .page(page.getNumber())
                        .totalElements(page.getTotalElements())
                        .isLast(page.isLast())
                        .size(page.getSize())
                        .totalPages(page.getTotalPages())
                        .numberOfElements(page.getNumberOfElements())
                        .build())
                .build();
    }

    public static Pageable getPageRequest(Pageable pageable){
        int pageSize = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(
                pageable.getPageNumber() -1,
                pageSize,
                pageable.getSort()
        );
    }

}//EC
