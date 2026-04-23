package com.kmicro.product.annotation;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle( HttpServletRequest request,  HttpServletResponse response,  Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) return true;

        RequiresRole annotation = handlerMethod.getMethodAnnotation(RequiresRole.class);

        // 2. If not on Method, try to get it from the Class (Controller)
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(RequiresRole.class);
        }

        // If no annotation, path is public/open
        if (annotation == null) return true;

        String rolesHeader = request.getHeader("x-auth-user-roles"); // Sent by Gateway
        String userId = request.getHeader("x-auth-user-id"); // Sent by Gateway
        log.info("UserID: {} -- Roles: {}",rolesHeader,userId);
        if (rolesHeader == null) {
            response.sendError(401, "Missing Auth Roles");
            return false;
        }

        List<String> userRoles = Arrays.asList(rolesHeader.split(","));
        boolean hasAccess = Arrays.stream(annotation.value())
                .anyMatch(userRoles::contains);

        if (!hasAccess) {
            response.sendError(403, "Access Denied: Insufficient Roles");
            return false;
        }
        return true;
    }
}