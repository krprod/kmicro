package com.kmicro.user.utils;

import com.kmicro.user.exception.JWTFailureException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthUtil {
    private final TokenBlackListing tokenBlacklistService;
    private final JwtUtil jwtUtil;

    private static final String BEARER_PREFIX = "Bearer ";

    public String extractTokenFromRequest(HttpServletRequest request){
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header");
            throw new JWTFailureException("Missing or malformed Authorization header");
        }
        return   authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }

    public Claims invalidateToken(String token){
        Claims claims = jwtUtil.getClaims(token);
        tokenBlacklistService.blacklistToken(token);
        return claims;
    }

    public void blackListToken(String token){
        tokenBlacklistService.blacklistToken(token);
    }

    public Claims getClaims(String token){
        return jwtUtil.getClaims(token);
    }

    public Claims getClaimsAndInvalidate(HttpServletRequest request){
        return invalidateToken(this.extractTokenFromRequest(request));
    }

    public String generateToken(Authentication authenticationResponse) {
        return  jwtUtil.generateToken(authenticationResponse);
    }

//    public String cleanAndSanitize(String data){
//        return ;
//    }

}
