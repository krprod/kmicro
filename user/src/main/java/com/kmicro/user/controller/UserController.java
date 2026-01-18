package com.kmicro.user.controller;

import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.dtos.UserDetailUpdateRec;
import com.kmicro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Handles  User Email, Password, Avtar, Lat-Long Updates")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Get Existing User Details, With or Without Address Param")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data Fetched Successful"),
            @ApiResponse(responseCode = "400", description = "Global Error Handles")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @RequestParam(required = false) String withAddress, @PathVariable(name = "id") Long id){

        UserDTO user =withAddress != null && withAddress.equalsIgnoreCase("true")?
                userService.getUserById(id, true) :
                userService.getUserById(id, false);

        return ResponseEntity.status(200).body(user);
    }

    @Operation(summary = "Delete, Lock and Invalidate JWT of current JWT Request Token Holder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deletion Successful"),
            @ApiResponse(responseCode = "400", description = "Global Error Handles")
    })
    @DeleteMapping
    public ResponseEntity<String>deleteUser(HttpServletRequest request){
        userService.deleteUser(request);
        return ResponseEntity.ok("Success");
    }

    @Operation(summary = "Update Existing User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updation Successful"),
            @ApiResponse(responseCode = "400", description = "Global Error Handles")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO>updateExistingUser(@RequestBody UserDetailUpdateRec userDetailUpdateRec, @PathVariable(name = "id") Long id){
        UserDTO userDTO  = userService.updateExistingUser(userDetailUpdateRec, id);
        return ResponseEntity.ok(userDTO);
    }


}//EC
