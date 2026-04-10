package com.example.usermanagementsystem.controller;

import com.example.usermanagementsystem.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // constructor injection 
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<String> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public String addUser(@RequestParam String name) {
        return userService.addUser(name);
    }

        @GetMapping("/add")
    public String addUserFromBrowser(@RequestParam String name) {
        return userService.addUser(name);

    }

    @GetMapping("get/{id}")
    public String getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }
}