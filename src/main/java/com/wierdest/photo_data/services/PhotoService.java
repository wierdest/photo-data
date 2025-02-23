package com.wierdest.photo_data.services;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.StorageException;
import com.wierdest.photo_data.converters.BlobToPhotoDTOConverter;
import com.wierdest.photo_data.dtos.InfoDTO;
import com.wierdest.photo_data.dtos.PhotoDTO;
import com.wierdest.photo_data.exceptions.InvalidFormatException;
import com.wierdest.photo_data.exceptions.UploadPhotoException;
import com.wierdest.photo_data.facades.BucketFacade;

@Service
public class PhotoService {
    public PhotoDTO uploadPhoto(MultipartFile file, String projectId, String bucketName, InfoDTO info) throws InvalidFormatException {
        try {
            if(!isValidFormat(file)) {
                throw new InvalidFormatException("Invalid file format. Only JPEG and PNG allowed");
            }
            Blob blob = BucketFacade.getInstance().uploadObject(projectId, bucketName, extractNameFromInfoDTODescriptor(info), file.getBytes());
            return new BlobToPhotoDTOConverter(blob).convert();
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

    private String extractNameFromInfoDTODescriptor(InfoDTO info) {
        return info.descriptor().split(" ")[0];
    }
}
