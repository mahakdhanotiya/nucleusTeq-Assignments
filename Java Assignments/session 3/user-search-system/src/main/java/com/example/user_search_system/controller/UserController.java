package com.example.user_search_system.controller;

import com.example.user_search_system.model.User;
import com.example.user_search_system.service.UserService;
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
    public List<User> searchUsers(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String role
    ) {
        return userService.searchUsers(id, name, age, role);
    }
}