package com.kmicro.order.dtos;

import org.springframework.http.HttpStatus;

public record ErrorResponseDTO(
          String apiPath,
         HttpStatus errorCode,
          String errorMessage,
         String errorTime){ }
