package com.kmicro.order.exception;


import com.kmicro.order.dtos.ErrorResponseDTO;
import com.kmicro.order.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();

        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });
        log.error("Validation Errror Request at {}: {}", request.getDescription(false), validationErrors);
        return  ResponseEntity.status(400).body(validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception exception, WebRequest webRequest) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                DateUtil.getTimeStampWithFormat("gen")
        );
        log.error("Server Errror Request at {}: {}", webRequest.getDescription(false), exception.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({DataNotFoundException.class,  OrderException.class, CartException.class})
    public ResponseEntity<ErrorResponseDTO>handleNotFound(Exception exception, WebRequest webRequest){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webRequest.getDescription(false),
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                DateUtil.getTimeStampWithFormat("gen")
        );
        log.error("DataNotFoundException Occured, Request at {}: {}", webRequest.getDescription(false), exception.getMessage());
        return  ResponseEntity.status(400).body(errorResponseDTO);
    }


//    @ExceptionHandler({
//            CartException.class,
//            OrderException.class,
//            DataNotFoundException.class
//    })
//    public ResponseEntity<ErrorResponseDTO>handleOrderEx(OrderException exception, WebRequest webRequest){
//
//        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
//                webRequest.getDescription(false),
//                HttpStatus.NOT_FOUND,
//                exception.getMessage(),
//                DateUtil.getTimeStampWithFormat("gen")
//        );
//        log.error("OrderException Occured, Request at {}: {}", webRequest.getDescription(false), exception.getMessage());
//        return  ResponseEntity.status(400).body(errorResponseDTO);
//    }
}
