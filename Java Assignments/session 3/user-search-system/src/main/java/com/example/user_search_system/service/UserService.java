package com.example.user_search_system.service;

import com.example.user_search_system.model.User;
import com.example.user_search_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


 // Service class to handle business logic

@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor Injection 

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


     // Method to search users based on name, age, and role
     
    public List<User> searchUsers(Integer id, String name, Integer age, String role) {

        return userRepository.getAllUsers()
                .stream()
                .filter(user ->
                        (id == null || user.getId().equals(id)) &&
                        (name == null || user.getName().equalsIgnoreCase(name)) &&
                        (age == null || user.getAge().equals(age)) &&
                        (role == null || user.getRole().equalsIgnoreCase(role))
                )
                .collect(Collectors.toList());
    }
}