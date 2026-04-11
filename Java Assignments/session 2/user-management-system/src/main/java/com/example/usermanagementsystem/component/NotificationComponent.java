package com.example.usermanagementsystem.component;

import org.springframework.stereotype.Component;

// Component for handling notification logic
@Component
public class NotificationComponent {

    public String sendNotification() {
        return "Notification sent successfully";
    }
}