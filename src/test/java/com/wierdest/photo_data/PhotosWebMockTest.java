package com.wierdest.photo_data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wierdest.photo_data.controllers.PhotoController;
import com.wierdest.photo_data.dtos.InfoDTO;
import com.wierdest.photo_data.dtos.PhotoDTO;
import com.wierdest.photo_data.exceptions.InvalidFormatException;
import com.wierdest.photo_data.exceptions.UploadPhotoException;
import com.wierdest.photo_data.services.PhotoService;
import com.wierdest.photo_data.utils.UploadUtils;

@WebMvcTest(PhotoController.class)
class PhotosWebMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PhotoService service;
        
    @Test
    void uploadPhotosShouldReturnPhotoDTO() throws Exception {
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

        PhotoDTO mockPhotoDTO = new PhotoDTO("some-name", "some-link");

        when(service.uploadPhoto(mockFile)).thenReturn(mockPhotoDTO);

        this.mockMvc.perform(
            multipart("/photos/upload")
            .file(mockFile)     
            .part(mockInfoPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("some-name"))
        .andExpect(jsonPath("$.link").value("some-link"));
    }

    @Test
    void uploadPhotosShouldThrowInvalidFormatException() throws Exception {

        when(service.uploadPhoto(any(MultipartFile.class)))
        .thenThrow(new InvalidFormatException("Invalid file format. Only JPEG and PNG allowed"));

        MockMultipartFile mockFile = new MockMultipartFile(
            "file", // Name of the file part
            "test-image.gif", // Notice the invalid format, .gif
            MediaType.IMAGE_GIF_VALUE, // Content type
            "Test file content".getBytes() // File content
        );

        InfoDTO mockInfoDTO = new InfoDTO();
        mockInfoDTO.setDescriptor("some descriptor string");

        ObjectMapper objectMapper = new ObjectMapper();
        String mockInfoJSON = objectMapper.writeValueAsString(mockInfoDTO);

        MockPart mockInfoPart = new MockPart("info", mockInfoJSON.getBytes());
        mockInfoPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(
            multipart("/photos/upload")
            .file(mockFile)     
            .part(mockInfoPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid file format. Only JPEG and PNG allowed"));
    }

    @Test
    void uploadPhotosShouldThrowUploadPhotoExceptionForStorageException() throws Exception {
        // Create a mock file
        MockMultipartFile mockFile = new MockMultipartFile(
            "file", // Name of the file part
            "test-image.jpg", // Original file name
            MediaType.IMAGE_JPEG_VALUE, // Content type
            "Test file content".getBytes() // File content
        );

        // Mock the service to throw a StorageException
        when(service.uploadPhoto(any(MultipartFile.class)))
            .thenThrow(new UploadPhotoException("Failed to upload photo due to a storage error!"));

        // Create a mock InfoDTO
        InfoDTO mockInfoDTO = new InfoDTO();
        mockInfoDTO.setDescriptor("some descriptor string");

        // Convert InfoDTO to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String mockInfoJSON = objectMapper.writeValueAsString(mockInfoDTO);

        // Create a mock Part for the InfoDTO
        MockPart mockInfoPart = new MockPart("info", mockInfoJSON.getBytes());
        mockInfoPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Perform the request and verify the response
        this.mockMvc.perform(
            multipart("/photos/upload")
                .file(mockFile) // Add the file part
                .part(mockInfoPart) // Add the info part
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Failed to upload photo due to a storage error!"));
    }
    
}
