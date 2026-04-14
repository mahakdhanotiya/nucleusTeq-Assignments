package com.example.user_search_system.service;

import com.example.user_search_system.model.User;
import com.example.user_search_system.repository.UserRepository;
import com.example.user_search_system.exception.InvalidUserException;
import com.example.user_search_system.exception.UserNotFoundException;

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


    // Method to add a new user

    public User addUser(User user) {

        // Basic validation

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidUserException("Name cannot be empty");
         }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new InvalidUserException("Role cannot be empty");
        }

        if (user.getAge() == null) {
            throw new InvalidUserException("Age is required");
        }

        userRepository.getAllUsers().add(user);
        return user;

    }


    // Method to delete user by id
 
    public String deleteUser(Integer id, Boolean confirm) {

        // Check confirmation
        if (confirm == null || !confirm) {
            return "Confirmation required";
        }

        List<User> users = userRepository.getAllUsers();

        boolean removed = users.removeIf(user -> user.getId().equals(id));

        if (!removed) {
           throw new UserNotFoundException("User not found");
        }

        return "User deleted successfully";
    }

}