package com.example.usermanagementsystem.component;

import org.springframework.stereotype.Component;

@Component
public class NotificationComponent {

    public String sendNotification() {
        return "Notification sent successfully";
    }
}