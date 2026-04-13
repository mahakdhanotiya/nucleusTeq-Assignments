package com.example.user_search_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// User model class representing user data

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
 
    private Integer id;   // user id
    private String name;  // user name
    private Integer age;  // user age
    private String role;  //user role
}