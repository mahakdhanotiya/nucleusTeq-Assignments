package com.example.usermanagementsystem.controller;

import com.example.usermanagementsystem.service.MessageFormatterService;
import org.springframework.web.bind.annotation.*;

// REST controller for message formatting APIs

@RestController
@RequestMapping("/formatter")
public class MessageFormatterController {

    private final MessageFormatterService service;

    public MessageFormatterController(MessageFormatterService service) {
        this.service = service;
    }

    // API to format message based on type (short or long)

    @GetMapping
    public String formatMessage(@RequestParam String message,
                                @RequestParam String type) {
        return service.formatMessage(message, type);
    }
}