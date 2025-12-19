package com.kmicro.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record BoughtProductRecord(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) @Positive long id,
        @Schema(description = "Quantity of the product", example = "2")@Min(1) int qty) {}
