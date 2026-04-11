package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.repository.UserRepository;
import com.example.usermanagementsystem.exception.UserNotFoundException;
import com.example.usermanagementsystem.exception.UserAlreadyExistsException;
import com.example.usermanagementsystem.exception.InvalidUserDataException;

import org.springframework.stereotype.Service;

import java.util.List;

// Service layer for handling all user-related operations such as CRUD and validation
@Service
public class UserService {

    private final UserRepository repo;

    //Constructor Injection
    public UserService(UserRepository repo) {
        this.repo = repo;

    }


    //----- GET ALL USERS ------

    public List<User> getAllUsers() {
        return repo.getAllUsers();
    }

    
    //------ GET USER BY ID ------

    public User getUserById(int id) {

        // Fetch user by ID and throw exception if not found
        User user = repo.getUserById(id);

        if (user == null) {
           throw new UserNotFoundException("User not found with id: " + id);
        }
        return user;
    }


    // -----ADD USER ------

    public String addUser(User user) {

        // Validate user input 

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidUserDataException("User name cannot be empty");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new InvalidUserDataException("User email cannot be empty");
        }
       //Check for duplicate user ID

        User existingUser = repo.getUserById(user.getId());

        if (existingUser != null) {
            throw new UserAlreadyExistsException("User with this ID already exists");
        }
        repo.addUser(user);
        return "User added successfully";
    }


    //------DELETE USER------

    public String deleteUser(int id) {

        //Check if user exists before deleting
        User user = repo.getUserById(id);

        if (user == null) {
           throw new UserNotFoundException("User not found with id: " + id);
        }
        repo.deleteUser(id);
        return "User deleted successfully";
    }


    //------ UPDATE USER-------

    public String updateUser(int id, String name, String email) {

        // Check if user exists before updating
        User user = repo.getUserById(id);

        if (user == null) {
           throw new UserNotFoundException("User not found with id: " + id);
        }

        repo.updateUser(id, name, email);
        return "User updated successfully";
    }

}