package com.kmicro.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PagedResponseDTO<T> {
    private List<T> content;
    private Metadata metadata;

    @Data
    @Builder
    public static class Metadata {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean isLast;
        private int numberOfElements;
    }
}
