package com.kmicro.user.service;

import com.kmicro.user.dtos.LoginRequest;
import com.kmicro.user.dtos.LoginResponse;
import com.kmicro.user.dtos.ResponseDTO;
import com.kmicro.user.dtos.UserRegistrationRecord;
import com.kmicro.user.utils.UserAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserAuthUtil userAuthUtil;
    private final UserService userService;


    public LoginResponse processLogin(LoginRequest loginRequest){

        Authentication authRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.email(), loginRequest.password());

        Authentication authenticationResponse = this.authenticationManager.authenticate(authRequest);

        if(null != authenticationResponse && authenticationResponse.isAuthenticated()){
            final String  jwt = userAuthUtil.generateToken(authenticationResponse);
            userService.updateFieldsOnLogin(loginRequest.email());
            log.info("Login successful for user: {}", loginRequest.email());
            return new LoginResponse(HttpStatus.OK.getReasonPhrase(), jwt,"Login Success", loginRequest.email());
        }
        log.info("Login failed for user: {}", loginRequest.email());
        return new LoginResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "", "Login Failed", loginRequest.email());
    }

    public void removeAuthToken(HttpServletRequest request) {
            userAuthUtil.blackListToken(userAuthUtil.extractTokenFromRequest(request));
            SecurityContextHolder.clearContext();
    }

    public ResponseDTO createUser(UserRegistrationRecord user) {
        return userService.createUser(user);
    }

    public boolean LoginNameExists(String loginName) {
        return userService.LoginNameExists(loginName);
    }
} //EC
