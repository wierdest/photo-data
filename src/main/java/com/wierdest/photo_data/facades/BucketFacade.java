package com.wierdest.photo_data.facades;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

public class BucketFacade {

    private BucketFacade() {}

    private static class SingletonHelper {
        private static final BucketFacade INSTANCE = new BucketFacade();
    }

    public static BucketFacade getInstance() {
        return SingletonHelper.INSTANCE;
    }


    public Blob uploadObject(String projectId, String bucketName, String objectName, byte[] filePath) throws StorageException {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        // Generation-match precondition to avoid race conditions and data corruptions
        // Returns 412 if the preconditions are not met
        Storage.BlobTargetOption precondition;
        if(storage.get(bucketName, objectName) == null) {
            // For a target object that does not yet exist, set the DoesNotExist precondition.
            // This will cause the request to fail if the object is created before the request runs.
            precondition = Storage.BlobTargetOption.doesNotExist();
        } else {
            // If the destination already exists in the bucket, set a generation-match precondition.
            // This will cause the request to fail if the existing object's generation changes before the request runs.
            precondition = Storage.BlobTargetOption.generationMatch(storage.get(bucketName, objectName).getGeneration());
        }
        return storage.create(blobInfo, filePath, precondition);
    }
}
