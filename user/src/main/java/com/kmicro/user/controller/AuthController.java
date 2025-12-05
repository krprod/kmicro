package com.kmicro.user.controller;

import com.kmicro.user.constants.ApplicationConstants;
import com.kmicro.user.dtos.LoginRequest;
import com.kmicro.user.dtos.LoginResponse;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.security.utils.JwtUtil;
import com.kmicro.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    final private AuthenticationManager authenticationManager;
    final private UserDetailsService userDetailsService;
    final  private JwtUtil jwtUtil;
    final  private  UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {

        // 1. Authenticate the user credentials
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password())
//        );
        Authentication authRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.useremail(), loginRequest.password());

        Authentication authenticationResponse = this.authenticationManager.authenticate(authRequest);

        if(null != authenticationResponse && authenticationResponse.isAuthenticated()){
//            System.out.println(authenticationResponse);
            final String  jwt = jwtUtil.generateToken(loginRequest.useremail());

            return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER,jwt)
                    .body(new LoginResponse(HttpStatus.OK.getReasonPhrase(), jwt));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "FallDOwn"));
        // 2. Load UserDetails and generate the JWT
//        final UserDetails userDetails = userDetailsService
//                .loadUserByUsername(loginRequest.username());

        // 3. Return the JWT to the client

    }


    @PostMapping("/logout")
    public ResponseEntity<?> removeAuthToken(){
        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<String> createNewUser(@RequestBody UserDTO user){
        userService.createUser(user);
        return null;
    }
}
