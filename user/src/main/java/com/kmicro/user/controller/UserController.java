package com.kmicro.user.controller;

import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public String createUser(@RequestBody UserDTO user){
        userService.createUser(user);
        return "User created";
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "id") Long id){
        UserDTO user = userService.getUserByIdWithAddress(id);
        return ResponseEntity.status(200).body(user);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        var user = userService.getAllUsers();
        return ResponseEntity.status(200).body(user);
    }


    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Long id){
        userService.deleteUser(id);
        return "User Deleted";
    }
}
