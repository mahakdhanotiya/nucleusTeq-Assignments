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

    public List<User> getAllUsers() {
        return users;
    }

    public User getUserById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void deleteUser(int id) {
        users.removeIf(u -> u.getId() == id);
    }

    public void updateUser(int id, String name, String email) {
        User user = getUserById(id);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
        }
    }
}