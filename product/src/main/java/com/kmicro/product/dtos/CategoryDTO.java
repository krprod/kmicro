package com.kmicro.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CategoryDTO {
    private long id;

    @NonNull
    private String name;
    @NonNull
    private String slug;

    @JsonProperty(value = "active")
    @NonNull
    private boolean is_active;
}
