package com.kmicro.user.exception;


import com.kmicro.user.dtos.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // Populate dynamic values
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (authException != null && authException.getMessage() != null) ? authException.getMessage()
                : "Unauthorized";
        String path = request.getRequestURI();
//        response.setHeader("eazybank-error-reason", "Authentication failed");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        // Construct the JSON response
        String jsonResponse =
                String.format("{\"errorTime\": \"%s\", \"errorCode\": %d, \"errorMessage\": \"%s\", \"apiPath\": \"%s\"}",
                        currentTimeStamp, HttpStatus.UNAUTHORIZED.value(), message, path);

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(path, HttpStatus.UNAUTHORIZED,
                "Authentication Failed,", currentTimeStamp);
//        log.error(errorResponseDTO.toString());
        log.error("Actual Msg1: {}",message);
        log.error("AuthenticationException1:",authException);
        response.getWriter().write(jsonResponse);
//        response.getWriter().write(errorResponseDTO.toString());
    }
}
