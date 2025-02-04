package com.wierdest.photo_data.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wierdest.photo_data.DTOs.InfoDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/photos")
public class PhotoController {
    
   @PostMapping("/upload")
   public ResponseEntity<String> uploadPhotos(@RequestPart("file") MultipartFile file, @RequestPart("info") InfoDTO info) {       
        return ResponseEntity.ok("Upload endpoint is working!");
   }
    
}
