package com.wierdest.photo_data.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/photos")
public class PhotoController {
    
   @PostMapping("/upload")
   public ResponseEntity<String> uploadPhotos(@RequestBody String entity) {
       return ResponseEntity.ok("Upload endpoint is working!");
   }
    
}
