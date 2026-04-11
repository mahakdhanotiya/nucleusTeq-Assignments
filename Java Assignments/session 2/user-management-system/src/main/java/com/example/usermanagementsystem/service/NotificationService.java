package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.component.NotificationComponent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationComponent notificationComponent;

    public NotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = notificationComponent;
    }

    public String sendNotification() {
        return notificationComponent.sendNotification();
    }
}