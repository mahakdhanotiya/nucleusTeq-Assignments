package com.example.usermanagementsystem.controller;

import com.example.usermanagementsystem.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET all users
    @GetMapping
    public List<String> getUsers() {
        return userService.getAllUsers();
    }

    // ADD user (POST - proper)
    @PostMapping
    public String addUser(@RequestParam String name) {
        return userService.addUser(name);
    }

    // ADD user (browser)
    @GetMapping("/add")
    public String addUserBrowser(@RequestParam String name) {
        return userService.addUser(name);
    }

    // GET by id
    @GetMapping("/get/{id}")
    public String getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }

    // DELETE (proper)
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

    // DELETE (browser)
    @GetMapping("/delete")
    public String deleteUserBrowser(@RequestParam int id) {
        return userService.deleteUser(id);
    }

    // UPDATE (proper)
    @PutMapping("/{id}")
    public String updateUser(@PathVariable int id, @RequestParam String name) {
        return userService.updateUser(id, name);
    }

    // UPDATE (browser)
    @GetMapping("/update")
    public String updateUserBrowser(@RequestParam int id, @RequestParam String name) {
        return userService.updateUser(id, name);
    }
}