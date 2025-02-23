package com.wierdest.photo_data;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wierdest.photo_data.controllers.PhotoController;
import com.wierdest.photo_data.dtos.InfoDTO;
import com.wierdest.photo_data.dtos.PhotoDTO;
import com.wierdest.photo_data.exceptions.InvalidFormatException;
import com.wierdest.photo_data.exceptions.UploadPhotoException;
import com.wierdest.photo_data.services.PhotoService;

@WebMvcTest(PhotoController.class)
class PhotoControllerTest {

    @Autowired
    private PhotoController photoController;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PhotoService service;

    private MockMultipartFile validFile;

    private MockMultipartFile invalidFile;

    private InfoDTO infoDTO;

    private MockPart infoPart;

    private PhotoDTO photoDTO;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        // Remember!! use reflection to set the @Value properties in the controller, so the mock works 
        ReflectionTestUtils.setField(photoController, "projectId", "project-id");
        ReflectionTestUtils.setField(photoController, "bucketName", "bucket-name");

        validFile = new MockMultipartFile(
            "file", 
            "test-image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "Test file content".getBytes() 
        );

        invalidFile = new MockMultipartFile(
            "file", 
            "test-image.gif",
            MediaType.IMAGE_GIF_VALUE,
            "Test file content".getBytes()
        );

        ObjectMapper objectMapper = new ObjectMapper();
        infoDTO = new InfoDTO("some-file descriptor string");
        String mockInfoJSON = objectMapper.writeValueAsString(infoDTO);
        infoPart = new MockPart("info", mockInfoJSON.getBytes());
        infoPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        photoDTO = new PhotoDTO("some-name", "some-link");
    }

        
    @Test
    void uploadPhotosShouldReturnPhotoDTO() throws Exception {
        when(service.uploadPhoto(validFile, "project-id", "bucket-name", infoDTO)).thenReturn(photoDTO);
        
        mockMvc.perform(
            multipart("/photos/upload")
            .file(validFile)     
            .part(infoPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("some-name"))
        .andExpect(jsonPath("$.link").value("some-link"));
    }

    @Test
    void uploadPhotosShouldThrowInvalidFormatException() throws Exception {
        when(service.uploadPhoto(invalidFile, "project-id", "bucket-name", infoDTO)).thenThrow(new InvalidFormatException("Invalid file format. Only JPEG and PNG allowed"));
        
        mockMvc.perform(
            multipart("/photos/upload")
            .file(invalidFile)     
            .part(infoPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid file format. Only JPEG and PNG allowed"));
    }

    @Test
    void uploadPhotosShouldThrowUploadPhotoException() throws Exception {
        when(service.uploadPhoto(validFile, "project-id", "bucket-name", infoDTO)).thenThrow(new UploadPhotoException("Failed to upload photo due to a storage error!"));

        mockMvc.perform(
            multipart("/photos/upload")
                .file(validFile)
                .part(infoPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Failed to upload photo due to a storage error!"));
    }   
}
