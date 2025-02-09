package com.wierdest.photo_data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.wierdest.photo_data.utils.UploadUtils;

class UploadUtilsUploadObjectTest {

    @Mock
    private StorageOptions storageOptions;
    
    private static MockedStatic<StorageOptions> staticStorageOptions;

    @Mock
    private StorageOptions.Builder builder;
    
    @Mock
    private Storage storage;
    
    private String projectId = "test-project";
    private String bucketName = "test-bucket";
    private String objectName = "test-object";
    private String filePath = "test-file.txt";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Before each test        
        staticStorageOptions = mockStatic(StorageOptions.class);
        staticStorageOptions.when(StorageOptions::newBuilder).thenReturn(builder);
        when(builder.setProjectId(projectId)).thenReturn(builder);
        when(builder.build()).thenReturn(storageOptions);
        when(storageOptions.getService()).thenReturn(storage);
    }

    @AfterEach
    void close() {
        // Dereference the MockedStatic
        staticStorageOptions.close();
    }
    
    @Test
    void uploadObject_NewObject() throws Exception {
        when(storage.get(bucketName, objectName)).thenReturn(null);
        when(storage.createFrom(any(BlobInfo.class), any(Path.class), any(Storage.BlobWriteOption.class))).thenReturn(mock(Blob.class));

        // Act with mocked behaviour
        UploadUtils.uploadObject(projectId, bucketName, objectName, filePath);
        
        // Assert
        verify(storage, times(1)).get(bucketName, objectName);
        verify(storage, times(1)).createFrom(any(BlobInfo.class), any(Path.class), any(Storage.BlobWriteOption.class));
    }

   @Test
   void uploadObject_ExistingObject() throws IOException {
        // For an existing object, we should return a Blob
        Blob mockBlob = mock(Blob.class);
        when(mockBlob.getGeneration()).thenReturn(123L);

        when(storage.get(bucketName, objectName)).thenReturn(mockBlob);
        when(storage.createFrom(any(BlobInfo.class), any(Path.class), any(Storage.BlobWriteOption.class))).thenReturn(mock(Blob.class));
        
        UploadUtils.uploadObject(projectId, bucketName, objectName, filePath);

        // Assert that this time we call storage.get two times, one time to check if the object exists, another time to getGeneration()
        verify(storage, times(2)).get(bucketName, objectName);
        verify(storage, times(1)).createFrom(any(BlobInfo.class), any(Path.class), any(Storage.BlobWriteOption.class));
   }
}
