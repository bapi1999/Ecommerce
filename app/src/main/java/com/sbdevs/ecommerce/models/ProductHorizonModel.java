package com.sbdevs.ecommerce.models;

public class ProductHorizonModel {
    private String prOID;
    private String  productImg;
    private String title;
    private String coinUnit;
    private String priceUnit;

    public ProductHorizonModel(String prOID,String productImg, String title, String coinUnit, String priceUnit) {
        this.prOID = prOID;
        this.productImg = productImg;
        this.title = title;
        this.coinUnit = coinUnit;
        this.priceUnit = priceUnit;
    }

    public String getPrOID() {
        return prOID;
    }

    public void setPrOID(String prOID) {
        this.prOID = prOID;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoinUnit() {
        return coinUnit;
    }

    public void setCoinUnit(String coinUnit) {
        this.coinUnit = coinUnit;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }
}
