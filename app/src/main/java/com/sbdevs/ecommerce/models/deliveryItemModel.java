package com.sbdevs.ecommerce.models;

import java.util.ArrayList;
import java.util.List;

public class deliveryItemModel {
    public static final int CART_ITEM=0;
    public static final int TOTAL_AMOUNT_LAY=1;

    private int type;



    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
//TODO-CART_ITEM===============
    private String productID;
    private String productImage;
    private String productName;
    private String productPrice;
    private long productQty;
    private boolean inStock;
    private long maxQuantity;
    private long stockQuantity;
    private List<String> qtyIDS;

    public deliveryItemModel(int type,String productID, String productImage, String productName,
                             String productPrice, long productQty, boolean inStock,long maxQuantity,long stockQuantity) {
        this.type = type;
        this.productID = productID;
        this.productImage = productImage;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQty = productQty;
        this.inStock = inStock;
        this.maxQuantity = maxQuantity;
        this.stockQuantity = stockQuantity;
        qtyIDS = new ArrayList<>();
    }

    public long getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    public List<String> getQtyIDS() {
        return qtyIDS;
    }
    public void setQtyIDS(List<String> qtyIDS) {
        this.qtyIDS = qtyIDS;
    }
    public boolean isInStock() {
        return inStock;
    }
    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
    public String getProductID() {
        return productID;
    }
    public void setProductID(String productID) {
        this.productID = productID;
    }
    public String getProductImage() {
        return productImage;
    }
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public long getProductQty() {
        return productQty;
    }
    public void setProductQty(long productQty) {
        this.productQty = productQty;
    }

    public long getMaxQuantity() {
        return maxQuantity;
    }
    public void setMaxQuantity(long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
    //TODO-TOTAL_AMOUNT_LAY===============

    private int totalItem,totalItemPrice, totalAmpount;
    private String deliveryCharge;
    public deliveryItemModel(int type) {
        this.type = type;
    }

    public int getTotalItem() {
        return totalItem;
    }
    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public int getTotalItemPrice() {
        return totalItemPrice;
    }
    public void setTotalItemPrice(int totalItemPrice) {
        this.totalItemPrice = totalItemPrice;
    }

    public int getTotalAmpount() {
        return totalAmpount;
    }
    public void setTotalAmpount(int totalAmpount) {
        this.totalAmpount = totalAmpount;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }
    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
