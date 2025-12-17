package com.kmicro.user.controller;

import com.kmicro.user.constants.ApplicationConstants;
import com.kmicro.user.dtos.LoginRequest;
import com.kmicro.user.dtos.LoginResponse;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.security.utils.JwtUtil;
import com.kmicro.user.security.utils.TokenBlackListing;
import com.kmicro.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final  AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private  final UserService userService;
    private final TokenBlackListing tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {

        Authentication authRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.email(), loginRequest.password());

        Authentication authenticationResponse = this.authenticationManager.authenticate(authRequest);

        if(null != authenticationResponse && authenticationResponse.isAuthenticated()){
//            System.out.println(authenticationResponse);
//            final String  jwt = jwtUtil.generateToken(loginRequest.useremail());
            final String  jwt = jwtUtil.generateToken(authenticationResponse);
            userService.updateFieldsOnLogin(loginRequest.email());

            return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER,jwt)
                    .body(new LoginResponse(HttpStatus.OK.getReasonPhrase(), jwt,"Login Success", loginRequest.email()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new LoginResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "",
                                "Login Failed",
                                loginRequest.email()
                        ));
    }


    @PostMapping("/logout")
    public ResponseEntity<String> removeAuthToken(HttpServletRequest request){
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            tokenBlacklistService.blacklistToken(jwt);
        }

        // Clear security context (optional, as the token is blacklisted anyway)
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Successfully logged out and token invalidated.");
    }

    @PostMapping("/register")
    public ResponseEntity<String> createNewUser(@RequestBody UserDTO user){
        String response = userService.createUser(user);
        if(response == "success"){
            return ResponseEntity.status(201).body(response);
        }
        return ResponseEntity.status(400).body(response);
    }

    @GetMapping("/generate-csrf")
    public CsrfToken getCsrfToken(CsrfToken token) {
        // This triggers the deferred token to be generated/loaded
        return token;
    }

}//EC
