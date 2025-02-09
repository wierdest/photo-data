package com.wierdest.photo_data.utils;

import java.io.IOException;
import java.nio.file.Paths;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public final class UploadUtils {

    private UploadUtils() {
        throw new IllegalStateException("Cannot instantiate utility class!");
    }

    public static void uploadObject(String projectId, String bucketName, String objectName, String filePath) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        // Generation-match precondition to avoid race conditions and data corruptions
        // Returns 412 if the preconditions are not met
        Storage.BlobWriteOption precondition;
        if(storage.get(bucketName, objectName) == null) {
            // For a target object that does not yet exist, set the DoesNotExist precondition.
            // This will cause the request to fail if the object is created before the request runs.
            precondition = Storage.BlobWriteOption.doesNotExist();
        } else {
            // If the destination already exists in the bucket, set a generation-match precondition.
            // This will cause the request to fail if the existing object's generation changes before the request runs.
            precondition = Storage.BlobWriteOption.generationMatch(storage.get(bucketName, objectName).getGeneration());
        }
        storage.createFrom(blobInfo, Paths.get(filePath), precondition);
    }
    
    // TODO: uploadMany and uploadDirectory 
}
