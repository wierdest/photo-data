package com.wierdest.photo_data.services;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String getHelloMessage(String message) {
        if (message.isEmpty()) {
            return "Hello, World!";
        }
        return "Hello, " + message + "!";
    }
}
