package com.example.usermanagementsystem.controller;

import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    // Constructor Injection 
    public UserController(UserService service) {
        this.service = service;
    }

    // GET all users
    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    // GET user by id
    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return service.getUserById(id);
    }

    // CREATE user
    @PostMapping
    public String addUser(@RequestBody User user) {
        return service.addUser(user);
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        return service.deleteUser(id);
    }

    // UPDATE user
    @PutMapping("/{id}")
    public String updateUser(@PathVariable int id,
                             @RequestParam String name,
                             @RequestParam String email) {
        return service.updateUser(id, name, email);
    }
    
}