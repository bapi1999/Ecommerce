package com.sbdevs.ecommerce.uploadImages;

import java.util.List;

public class UploadImageModel {

    private String images;
    private String imageName;

    public UploadImageModel(String images, String imageName) {
        this.images = images;
        this.imageName = imageName;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
