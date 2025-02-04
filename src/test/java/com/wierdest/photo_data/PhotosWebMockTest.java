package com.wierdest.photo_data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import com.wierdest.photo_data.controllers.PhotoController;

@WebMvcTest(PhotoController.class)
class PhotosWebMockTest {

    @Autowired
    private MockMvc mockMvc;
    
}
