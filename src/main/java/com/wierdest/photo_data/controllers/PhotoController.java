package com.wierdest.photo_data.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wierdest.photo_data.DTOs.InfoDTO;
import com.wierdest.photo_data.services.PhotoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/photos")
public class PhotoController {
    private final PhotoService service;
    public PhotoController(PhotoService service) {
        this.service = service;
    }
    
   @PostMapping("/upload")
   public ResponseEntity<String> uploadPhotos(@RequestPart("file") MultipartFile file, @RequestPart("info") InfoDTO info) {       
        return ResponseEntity.ok(service.getPlaceholderSuccessfulMessage());
   }
    
}
