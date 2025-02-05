package com.wierdest.photo_data;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wierdest.photo_data.DTOs.InfoDTO;
import com.wierdest.photo_data.controllers.PhotoController;
import com.wierdest.photo_data.services.PhotoService;

@WebMvcTest(PhotoController.class)
class PhotosWebMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PhotoService service;

    @Test
    void uploadPhotosShouldReturnPlaceholderSuccessfulMessage() throws Exception {
        when(service.getPlaceholderSuccessfulMessage()).thenReturn("PhotoService exists!!!");

         MockMultipartFile mockFile = new MockMultipartFile(
                "file", // Name of the file part
                "test-image.jpg", // Original file name
                MediaType.IMAGE_JPEG_VALUE, // Content type
                "Test file content".getBytes() // File content
        );

        InfoDTO mockInfoDTO = new InfoDTO();
        mockInfoDTO.setDescriptor("some descriptor string");

        ObjectMapper objectMapper = new ObjectMapper();
        String mockInfoJSON = objectMapper.writeValueAsString(mockInfoDTO);

        MockPart mockInfoPart = new MockPart("info", mockInfoJSON.getBytes());
        mockInfoPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // mock the endpoint
        this.mockMvc.perform(multipart("/photos/upload")
           .file(mockFile)
           .part(mockInfoPart)
           .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk()).andExpect(content().string("PhotoService exists!!!"));
    }
    
}
