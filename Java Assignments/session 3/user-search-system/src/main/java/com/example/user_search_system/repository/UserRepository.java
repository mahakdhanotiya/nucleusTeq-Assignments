package com.example.user_search_system.repository;

import com.example.user_search_system.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


 // Repository class to manage user data (in-memory)
 
@Repository
public class UserRepository {

    // In-memory list acting as a fake database
    private List<User> users = new ArrayList<>();

    
     // Constructor to initialize dummy user data
     
    public UserRepository() {
       users.add(new User(1, "Mahak", 22, "USER"));
       users.add(new User(2, "Riya", 25, "ADMIN"));
       users.add(new User(3, "Aman", 30, "MANAGER"));
       users.add(new User(4, "Neha", 28, "USER"));
       users.add(new User(5, "Raj", 35, "ADMIN"));
       users.add(new User(6, "Simran", 24, "GUEST"));
       users.add(new User(7, "Arjun", 29, "MANAGER"));
       users.add(new User(8, "Priya", 26, "USER"));
       users.add(new User(9, "Karan", 32, "GUEST"));
       users.add(new User(10, "Sneha", 27, "ADMIN"));
    }
    
     //  Method to return all users
     
    public List<User> getAllUsers() {
        return users;
    }
}
