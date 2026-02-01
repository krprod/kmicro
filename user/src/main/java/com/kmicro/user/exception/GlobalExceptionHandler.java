package com.kmicro.user.exception;


import com.kmicro.user.dtos.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
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

        log.error("Valdiation Errors at {}: {}", request.getDescription(false), validationErrors);
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex, WebRequest webReq) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.error("Server Errror Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDTO);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntergrityException(DataIntegrityViolationException ex, WebRequest webReq){
        String message = null !=  ex.getRootCause().getMessage() ?  ex.getRootCause().getMessage() :   ex.getMessage();
        message = message.split("\n")[1].trim();
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                LocalDateTime.now()
        );

        log.error("DataIntegrityViolationException Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDTO);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleAlreadyExist(AlreadyExistException ex, WebRequest webReq){

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.CONFLICT,
                ex.getMessage(),
                LocalDateTime.now()
        );

        log.error("AlreadyExistException Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDTO);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, WebRequest webReq){

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now()
        );

        log.error("UserNotFoundException Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
    }

    @ExceptionHandler(NotExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(NotExistException ex, WebRequest webReq){

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now()
        );

        log.error("NotExistException Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponseDTO> handleRateLimit(RateLimitException ex, WebRequest webReq) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.TOO_MANY_REQUESTS,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponseDTO);
    }
    @ExceptionHandler(AccountDeactivated.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountDeactivated(AccountDeactivated ex, WebRequest webReq){

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now()
        );

        log.error("AccountDeactivated Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
    }

 @ExceptionHandler(AddressException.class)
    public ResponseEntity<ErrorResponseDTO> handleAddressEx(AddressException ex, WebRequest webReq){

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.error("Address Bad Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(VerificationExecption.class)
    public ResponseEntity<ErrorResponseDTO> handleAddressEx(VerificationExecption ex, WebRequest webReq){

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.error("VerificationExecption Bad Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationServiceError(InternalAuthenticationServiceException ex, WebRequest webReq){

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webReq.getDescription(false),
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.error("Unauthorized Request at {}: {}", webReq.getDescription(false), ex.getMessage());
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDTO);
    }

}//EC
