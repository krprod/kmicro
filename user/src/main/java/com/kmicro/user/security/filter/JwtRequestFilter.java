package com.kmicro.user.security.filter;

import com.kmicro.user.security.UserDetailServiceMs;
import com.kmicro.user.utils.JwtUtil;
import com.kmicro.user.utils.TokenBlackListing;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailServiceMs userDetailsService;
    private final  JwtUtil jwtUtil;
    private final TokenBlackListing tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)  throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

         String  jwt = authorizationHeader.substring(7); // Extract the token part

            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                logger.warn("Attempted use of blacklisted JWT.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalidated");
                return;
            }

            try {
                if( jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null){

                    User userDetails =  jwtUtil.extractUserFromToken(jwt);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, jwt, userDetails.getAuthorities());

                    // Set authentication details (like IP address, session id)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }else{
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                // Log or handle token parsing errors (e.g., expired, malformed)
                logger.warn("JWT is invalid or expired: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
