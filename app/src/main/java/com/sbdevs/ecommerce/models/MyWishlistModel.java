package com.sbdevs.ecommerce.models;

import java.util.ArrayList;

public class MyWishlistModel {

    private String productID;
    private String productImg;
    private String productTitle;
    private String avgRating;
    private long  totalRating;
    private  String productPriceBig;
    private String productPriceSml;
    private boolean inStock;
    private ArrayList<String> tags;

    public MyWishlistModel(String productID ,String productImg, String productTitle, String avgRating, long totalRating, String productPriceBig, String productPriceSml,boolean inStock) {
        this.productID =  productID;
        this.productImg = productImg;
        this.productTitle = productTitle;
        this.avgRating = avgRating;
        this.totalRating = totalRating;
        this.productPriceBig = productPriceBig;
        this.productPriceSml = productPriceSml;
        this.inStock = inStock;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImg() {
        return productImg;
    }
    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductTitle() {
        return productTitle;
    }
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getAvgRating() {
        return avgRating;
    }
    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public long getTotalRating() {
        return totalRating;
    }
    public void setTotalRating(long totalRating) {
        this.totalRating = totalRating;
    }

    public String getProductPriceBig() {
        return productPriceBig;
    }
    public void setProductPriceBig(String productPriceBig) {
        this.productPriceBig = productPriceBig;
    }

    public String getProductPriceSml() {
        return productPriceSml;
    }
    public void setProductPriceSml(String productPriceSml) {
        this.productPriceSml = productPriceSml;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
