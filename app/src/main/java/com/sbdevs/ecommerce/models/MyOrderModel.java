package com.sbdevs.ecommerce.models;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sbdevs.ecommerce.adapters.MyOrderAdapter;

public class MyOrderModel{
    private int productImg;
    private int rating;
    private String productName;
    private String deliveryStatus;

    public MyOrderModel(int productImg,int rating, String productName, String deliveryStatus) {
        this.productImg = productImg;
        this.rating = rating;
        this.productName = productName;
        this.deliveryStatus = deliveryStatus;
    }

    public int getProductImg() {
        return productImg;
    }
    public void setProductImg(int productImg) {
        this.productImg = productImg;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getDeliveryStatus() {
        return deliveryStatus;
    }
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
