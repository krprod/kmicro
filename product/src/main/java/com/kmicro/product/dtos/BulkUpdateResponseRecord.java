package com.kmicro.product.dtos;

import java.util.List;

public record BulkUpdateResponseRecord (
        List<Long> successIds,
        List<BulkErrorResponseRecord> errors) { }
