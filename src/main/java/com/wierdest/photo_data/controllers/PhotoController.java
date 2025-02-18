package com.wierdest.photo_data.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wierdest.photo_data.dtos.InfoDTO;
import com.wierdest.photo_data.dtos.PhotoDTO;
import com.wierdest.photo_data.exceptions.InvalidFormatException;
import com.wierdest.photo_data.exceptions.UploadPhotoException;
import com.wierdest.photo_data.services.PhotoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/photos")
public class PhotoController {
    private final PhotoService service;
    public PhotoController(PhotoService service) {
        this.service = service;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(@RequestPart("file") MultipartFile file, @RequestPart("info") InfoDTO info) {     
        try {
            return ResponseEntity.ok(service.uploadPhoto(file)); 
        } catch (InvalidFormatException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(UploadPhotoException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
}       
