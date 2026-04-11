package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.component.NotificationComponent;
import org.springframework.stereotype.Service;

// Service layer responsible for handling notification-related operations
@Service
public class NotificationService {

    private final NotificationComponent notificationComponent;

    public NotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = notificationComponent;
    }

    // Sends notification using NotificationComponent

    public String sendNotification() {
        return notificationComponent.sendNotification();
    }
}