package com.wierdest.photo_data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.StorageException;
import com.wierdest.photo_data.dtos.InfoDTO;
import com.wierdest.photo_data.dtos.PhotoDTO;
import com.wierdest.photo_data.exceptions.InvalidFormatException;
import com.wierdest.photo_data.exceptions.UploadPhotoException;
import com.wierdest.photo_data.facades.BucketFacade;
import com.wierdest.photo_data.services.PhotoService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

public class PhotoServiceTest {

    private PhotoService service;

    private MockMultipartFile validFile;

    private MockMultipartFile invalidFile;

    private InfoDTO infoDTO;

    @Mock
    private Blob blob;

    private MockedStatic<BucketFacade> bucketFacadeStaticMock;

    @Mock
    private BucketFacade facade;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PhotoService();

        infoDTO = new InfoDTO("some descriptor string");
        blob = mock(Blob.class);
       
        bucketFacadeStaticMock = mockStatic(BucketFacade.class);
        when(BucketFacade.getInstance()).thenReturn(facade);

        when(facade.uploadObject(
            eq("project-id"),
            eq("bucket-name"),
            eq("some"),
            any(byte[].class)
        )).thenReturn(blob);

        when(blob.getName()).thenReturn("some-name");
        when(blob.getMediaLink()).thenReturn("some-link");
    }

    @AfterEach
    void close() {
        bucketFacadeStaticMock.close();
    }


    @Test
    void uploadPhotoShouldReturnPhotoDTOForJPEG() throws Exception {

        validFile = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "dummy content".getBytes()
        );
        
        PhotoDTO result = service.uploadPhoto(validFile, "project-id", "bucket-name", infoDTO);
        assertNotNull(result);
        assertEquals("some-name", result.name());
        assertEquals("some-link", result.link());
      
    }

    @Test
    void uploadPhotoShouldReturnPhotoDTOForPNG() throws Exception {

        validFile = new MockMultipartFile(
            "file", "test.png", "image/png", "dummy content".getBytes()
        );
        
        PhotoDTO result = service.uploadPhoto(validFile, "project-id", "bucket-name", infoDTO);
        assertNotNull(result);
        assertEquals("some-name", result.name());
        assertEquals("some-link", result.link());
      
    }

    @Test
    void uploadPhotoShouldThrowInvalidFormat() {

        invalidFile = new MockMultipartFile(
            "file", "test.gif", "image/gif", "dummy content".getBytes()
        );

        InvalidFormatException exception = assertThrows(
            InvalidFormatException.class, 
            () -> service.uploadPhoto(invalidFile, "project-id", "bucket-name", infoDTO)
        );
        assertEquals("Invalid file format. Only JPEG and PNG allowed", exception.getMessage());
    }

    @Test
    void uploadPhotosShouldThrowUploadPhotoExceptionForStorageException() {

        validFile = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "dummy content".getBytes()
        );

        StorageException storageException = new StorageException(500, "Bucket error");

        when(facade.uploadObject(
            any(), any(), any(), any()
        )).thenThrow(storageException);

        UploadPhotoException exception = assertThrows(
            UploadPhotoException.class,
            () -> service.uploadPhoto(validFile, "project-id", "bucket-name", infoDTO)
        );
        assertNotEquals(null,  exception);
        assertEquals("Bucket error", exception.getMessage());
    }

    @Test
    void uploadPhotosShouldThrowUploadPhotoExceptionForIOException() {

        MockMultipartFile corruptFile = new MockMultipartFile(
            "file", "test.jpeg", "image/jpeg", (byte[]) null
        ) {
            @Override
            public byte[] getBytes() throws IOException {
                throw new IOException("Could not read file");
            }
        };

        InvalidFormatException exception = assertThrows(
            InvalidFormatException.class,
            () -> service.uploadPhoto(corruptFile, "project-id", "bucket-name", infoDTO)
        );

        assertNotEquals(null,  exception);
        assertEquals("Failed to read file content! Could not read file", exception.getMessage());    }

}