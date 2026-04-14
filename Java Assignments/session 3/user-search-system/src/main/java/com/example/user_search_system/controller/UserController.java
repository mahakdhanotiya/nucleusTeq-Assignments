package com.example.user_search_system.controller;

import com.example.user_search_system.model.User;
import com.example.user_search_system.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


 // Controller class to handle user-related APIs
 
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    
     // API to search users based on optional parameters
    
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String role
    ) {
        List<User> users = userService.searchUsers(id, name, age, role);

        return ResponseEntity.ok(users);
    }


    //  API to create a new user

    @PostMapping("/submit")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User savedUser = userService.addUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    // API to delete user by id with confirmation
 
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
           @PathVariable Integer id,
           @RequestParam(required = false) Boolean confirm
    ) {
        String response = userService.deleteUser(id, confirm);
        return ResponseEntity.ok(response);
    }
}

