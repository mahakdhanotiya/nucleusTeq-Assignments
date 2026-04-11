package com.example.usermanagementsystem.repository;

import com.example.usermanagementsystem.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

//Repository layer for managing user data

@Repository
public class UserRepository {

    private List<User> users = new ArrayList<>();

    // Dummy data
    
    public UserRepository() {
        users.add(new User(1, "Rahul", "rahul@gmail.com"));
        users.add(new User(2, "Aman", "aman@gmail.com"));
        users.add(new User(3, "Priya", "priya@gmail.com"));
    }

    //Fetch all users

    public List<User> getAllUsers() {
        return users;
    }

    //Fetch user by ID

    public User getUserById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    //Add new user

    public void addUser(User user) {
        users.add(user);
    }

    // Delete user by ID

    public void deleteUser(int id) {
        users.removeIf(u -> u.getId() == id);
    }

    //Update user details

    public void updateUser(int id, String name, String email) {
        User user = getUserById(id);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
        }
    }
}