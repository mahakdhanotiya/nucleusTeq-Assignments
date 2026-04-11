package com.example.usermanagementsystem.component;

import org.springframework.stereotype.Component;

//Component for formatting short messages

@Component
public class ShortMessageFormatter {

    public String formatMessage(String message) {
        return "SHORT: " + message;
    }
}