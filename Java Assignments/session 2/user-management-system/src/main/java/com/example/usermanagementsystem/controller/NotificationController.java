package com.example.usermanagementsystem.controller;

import com.example.usermanagementsystem.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public String sendNotification() {
        return service.sendNotification();
    }
}