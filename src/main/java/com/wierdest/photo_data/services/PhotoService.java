package com.wierdest.photo_data.services;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.StorageException;
import com.wierdest.photo_data.dtos.PhotoDTO;
import com.wierdest.photo_data.exceptions.InvalidFormatException;
import com.wierdest.photo_data.exceptions.UploadPhotoException;
import com.wierdest.photo_data.utils.UploadUtils;

@Service
public class PhotoService {

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.bucket-name}")
    private String bucketName;

    public PhotoDTO uploadPhoto(MultipartFile file) throws InvalidFormatException {

        try {

            if(!isValidFormat(file)) {
                throw new InvalidFormatException("Invalid file format. Only JPEG and PNG allowed");
            }
            
            Blob blob = UploadUtils.uploadObject(projectId, bucketName, bucketName, file.getBytes());
                    
            return new PhotoDTO(blob.getName(), blob.getMediaLink());

        } catch (IOException e) {
            throw new InvalidFormatException("Failed to read file content! " + e.getMessage(), e.getCause());
        } catch (StorageException e) {
            throw new UploadPhotoException(e.getMessage(), e.getCause());
        }
    }

    private boolean isValidFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return "image/jpeg".equals(contentType) || "image/png".equals(contentType);
    }
}
