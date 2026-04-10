package com.example.usermanagementsystem.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    private List<String> users = new ArrayList<>();

    // Dummy data 
    public UserService() {
        users.add("Rahul Sharma");
        users.add("Aman Verma");
        users.add("Priya Singh");
        users.add("Neha Gupta");
        users.add("Arjun Mehta");
    }

    public List<String> getAllUsers() {
        return users;
    }

    public String addUser(String name) {
        users.add(name);
        return "User added successfully";
    }

    public String getUserById(int id) {
        if (id < users.size()) {
            return users.get(id);
        }
        return "User not found";
    }
}
