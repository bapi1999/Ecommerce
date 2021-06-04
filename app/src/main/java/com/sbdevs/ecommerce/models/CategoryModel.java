package com.sbdevs.ecommerce.models;

public class CategoryModel {
    private String icon;
    private String catName;

    public CategoryModel(String icon, String catName) {
        this.icon = icon;
        this.catName = catName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
