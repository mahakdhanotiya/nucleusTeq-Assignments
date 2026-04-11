package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.component.ShortMessageFormatter;
import com.example.usermanagementsystem.component.LongMessageFormatter;
import org.springframework.stereotype.Service;

// Service to decide which formatter to use based on type

@Service
public class MessageFormatterService {

    private final ShortMessageFormatter shortFormatter;
    private final LongMessageFormatter longFormatter;

    public MessageFormatterService(ShortMessageFormatter shortFormatter,
                                   LongMessageFormatter longFormatter) {
        this.shortFormatter = shortFormatter;
        this.longFormatter = longFormatter;
    }

    // Returns formatted message based on type (short or long)

    public String formatMessage(String message, String type) {

        if (type.equalsIgnoreCase("short")) {
            return shortFormatter.formatMessage(message);
        } else {
            return longFormatter.formatMessage(message);
        }
    }
}