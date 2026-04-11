package com.example.usermanagementsystem.component;

import org.springframework.stereotype.Component;

// Component for formatting long messages

@Component
public class LongMessageFormatter {

    public String formatMessage(String message) {
        return "LONG MESSAGE: " + message + " - formatted successfully!";
    }
}