package com.kmicro.user.controller;

import com.kmicro.user.constants.AppContants;
import com.kmicro.user.dtos.LoginRequest;
import com.kmicro.user.dtos.LoginResponse;
import com.kmicro.user.dtos.ResponseDTO;
import com.kmicro.user.dtos.UserRegistrationRecord;
import com.kmicro.user.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication Controller", description = "Handles Login, Logout and New User Registration Requests")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login Authentication",
            description = "Authenticate User by Email And Password and return Generate JWT token for Authenticated Users ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfull Authentication"),
            @ApiResponse(responseCode = "400", description = "Authentication Failed")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> createAuthenticationToken(@Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse response = authService.processLogin(loginRequest);

        if(!response.token().isEmpty() && response.status().equalsIgnoreCase("ok")){
            ResponseEntity.status(HttpStatus.OK).header(AppContants.JWT_HEADER,response.token()).body(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Operation(summary = "LogOut Authenticated User",
            description = "Logs out current User and Blacklist  JWT token of Authenticated Users ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Logs out user"),
            @ApiResponse(responseCode = "400", description = "Failed GlobalHandler")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> removeAuthToken(HttpServletRequest request){
        authService.removeAuthToken(request);
        return ResponseEntity.ok("Successfully logged out and token invalidated.");
    }

    @Operation(summary = "Register/Create New User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User Created Successfully"),
            @ApiResponse(responseCode = "409", description = "Failed GlobalHandler")
    })
    @PostMapping("/register")
    public ResponseEntity<String> createNewUser(@RequestBody UserRegistrationRecord user){
        ResponseDTO response = authService.createUser(user);
        return ResponseEntity.status(201).body(response.getStatusMsg());
    }

    @Hidden
    @PostMapping("/login_exists")
    public ResponseEntity<String>loginNameExists(@RequestBody String loginName){
        if(!loginName.isEmpty() && loginName.length() > 3){
            if(authService.LoginNameExists(loginName)){
                return ResponseEntity.status(200).body("create");
            }
        }
        return ResponseEntity.status(400).body("Login Already Taken");
    }

    @Operation(summary = "Generate and Return New CSRF Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token Created Successfully"),
    })
    @GetMapping("/generate-csrf")
    public ResponseEntity<CsrfToken> getCsrfToken(CsrfToken token) {
        // This triggers the deferred token to be generated/loaded
        return ResponseEntity.status(200).body(token);
    }


    @Operation(summary = "User Email Verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data Fetched Successful"),
            @ApiResponse(responseCode = "400", description = "Global Error Handles")
    })
    @GetMapping("/verify")
    public ResponseEntity<Void> verifyUserEmail(@RequestParam(required = true) String token){
        authService.verifyUserEmail(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resend User Email Verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Data Fetched Successful"),
            @ApiResponse(responseCode = "409", description = "Global Error Handles")
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerificationMail(@RequestBody UserRegistrationRecord user){
        authService.resendVerificationMail(user);
        return ResponseEntity.noContent().build();
    }

//    @Operation(summary = "Delete, Lock and Invalidate JWT of current JWT Request Token Holder")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Deletion Successful"),
//            @ApiResponse(responseCode = "400", description = "Global Error Handles")
//    })
//    @PutMapping
//    public ResponseEntity<String>passwordReset(HttpServletRequest request){
//        .deleteUser(request);
//        return ResponseEntity.ok("Success");
//    }

}//EC
