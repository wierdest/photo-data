package com.wierdest.photo_data.converters;

import com.google.cloud.storage.Blob;
import com.wierdest.photo_data.dtos.PhotoDTO;

public class BlobToPhotoDTOConverter {

    private final Blob blob;

    public BlobToPhotoDTOConverter(Blob blob) {
        this.blob = blob;
    }

    public PhotoDTO convert() {
        // TODO learn about & convert metadata
        return new PhotoDTO(this.blob.getName(), this.blob.getMediaLink());
    }

}
