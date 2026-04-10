package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.repository.UserRepository;
import com.example.usermanagementsystem.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

//Service layer for handling business logic

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> getAllUsers() {
        return repo.getAllUsers();
    }

    public User getUserById(int id) {
        User user = repo.getUserById(id);

    if (user == null) {
        throw new UserNotFoundException("User not found with id: " + id);
    }
    return user;
    }

    public String addUser(User user) {
        repo.addUser(user);
        return "User added successfully";
    }

    public String deleteUser(int id) {
        repo.deleteUser(id);
        return "User deleted successfully";
    }

    public String updateUser(int id, String name, String email) {
        repo.updateUser(id, name, email);
        return "User updated successfully";
    }
}