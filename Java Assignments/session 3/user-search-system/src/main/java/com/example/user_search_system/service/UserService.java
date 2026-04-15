package com.example.user_search_system.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.user_search_system.exception.InvalidUserException;
import com.example.user_search_system.exception.UserAlreadyExistsException;
import com.example.user_search_system.exception.UserNotFoundException;
import com.example.user_search_system.model.User;
import com.example.user_search_system.repository.UserRepository;


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

        List<User> filteredUsers = userRepository.getAllUsers()
        .stream()
        .filter(user ->
                (id == null || user.getId().equals(id)) &&
                (name == null || user.getName().equalsIgnoreCase(name)) &&
                (age == null || user.getAge().equals(age)) &&
                (role == null || user.getRole().equalsIgnoreCase(role))
        )
        .collect(Collectors.toList());

       //  sorting added
        filteredUsers.sort(Comparator.comparing(User::getId));

        return filteredUsers;
    }



    // Method to add a new user

    public User addUser(User user) {

        // validation checks

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidUserException("Name cannot be empty");
         }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new InvalidUserException("Role cannot be empty");
        }

        if (user.getAge() == null) {
            throw new InvalidUserException("Age is required");
        }
        //  Duplicate ID check

        boolean exists = userRepository.getAllUsers()
                .stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (exists) {
            throw new UserAlreadyExistsException("User with this ID already exists");
        }

        // Add user

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

        return "User with ID " + id + " deleted successfully";
    }

}