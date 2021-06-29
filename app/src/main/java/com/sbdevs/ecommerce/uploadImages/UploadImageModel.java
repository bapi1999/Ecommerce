package com.sbdevs.ecommerce.uploadImages;

import java.util.List;

public class UploadImageModel {

    private String images;
    //private String imageName;

    public UploadImageModel(String images) {
        this.images = images;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
