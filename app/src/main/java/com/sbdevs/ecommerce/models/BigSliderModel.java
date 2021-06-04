package com.sbdevs.ecommerce.models;

public class BigSliderModel {
    private String banner;
    private String background;

    public BigSliderModel(String banner, String background) {
        this.banner = banner;
        this.background = background;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
