package com.kmicro.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(
        name = "Hold Response Data",
        description = "Hold Response Data "
)
@Data
@AllArgsConstructor
public class ResponseDTO {
    @Schema(
            description = "Status Code", example = "200 /400 /404"
    )
    private String statusCode;

    @Schema(
            description = "Status Message", example = "Success /Bad Request /Record Not Found"
    )
    private String statusMsg;
}
