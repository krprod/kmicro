package com.kmicro.product.dtos;

import java.util.List;

public record BulkResponseRecord(
        List<Long> successIds,
        List<BulkErrorResponseRecord> errors) { }
