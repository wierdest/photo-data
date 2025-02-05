package com.wierdest.photo_data.services;

import org.springframework.stereotype.Service;

@Service
public class PhotoService {
    
    public String getPlaceholderSuccessfulMessage() {
        return "Upload endpoint is working!";
    }
}
