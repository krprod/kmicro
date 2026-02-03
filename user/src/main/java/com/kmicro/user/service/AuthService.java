package com.kmicro.user.service;

import com.kmicro.user.constants.AppContants;
import com.kmicro.user.dtos.*;
import com.kmicro.user.entities.TokenEntity;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.exception.AlreadyExistException;
import com.kmicro.user.kafka.producers.ExternalEventProducers;
import com.kmicro.user.mapper.UserMapper;
import com.kmicro.user.utils.UserAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserAuthUtil userAuthUtil;
    private final UserService userService;
    private final VerificationService verificationService;
    private final ExternalEventProducers externalEventProducers;

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

    @CachePut(value = AppContants.CACHE_USER_KEY_PX, key = "#result.getId()")
    @Transactional
    public UserDTO verifyUserEmail(String token) {
        TokenEntity verificationToken = verificationService.verifyToken(token);
        UserEntity userEntity = userService.getUserById(verificationToken.getUserId());
        if(!userEntity.isActive() && !userEntity.isVerified()){
            // if user verified false -> set verified=true & active=true
            userEntity.setActive(true);
            userEntity.setVerified(true);
            verificationToken.setIsVerified(true);
            verificationService.updateToken(verificationToken);
        }
        UserEntity savedUser= userService.saveActivatedVerifiedUser(userEntity);
        return UserMapper.EntityWithAddressToDTOWithAddress(savedUser);
    }

    @Transactional
    public void resendVerificationMail(Long userID) {
        UserEntity user = userService.getUserById(userID);
        if(!user.isVerified() && !user.isActive()){
            // resend Verification Mail
            String link = verificationService.getAttemptedVerificationLink(user.getId());
            externalEventProducers.emailVerificationNotification(user, link,"verifyReattempt_");
        }else {
            throw  new AlreadyExistException("User Already Verified.");
        }
    }
} //EC
