package com.sbdevs.ecommerce.ui.home;

import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sbdevs.ecommerce.models.BigSliderModel;
import com.sbdevs.ecommerce.models.MyWishlistModel;
import com.sbdevs.ecommerce.models.ProductHorizonModel;

import java.util.ArrayList;
import java.util.List;

public class HomeModel {

    public static final int BANNER_ADS = 0;
    public static final int BIG_SLIDER = 1;
    public static final int PRODUCT_HORIZONTAL = 2;
    public static final int PRODUCT_GRID = 3;


    private int type;
    //TODO- BIG SLIDER =====================================================================================================================
    private List<BigSliderModel> bigSliderList;

    public HomeModel(int type, List<BigSliderModel> bigSliderList) {
        this.type = type;
        this.bigSliderList = bigSliderList;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public List<BigSliderModel> getBigSliderList() {
        return bigSliderList;
    }
    public void setBigSliderList(List<BigSliderModel> bigSliderList) {
        this.bigSliderList = bigSliderList;
    }

    //TODO- BANNER ADS =====================================================================================================================
    private String bannerImg;
    private String bannerColor;

    public HomeModel(int type, String  bannerImg, String bannerColor) {
        this.type = type;
        this.bannerImg = bannerImg;
        this.bannerColor = bannerColor;
    }
    public String getBannerImg() {
        return bannerImg;
    }
    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }
    public String getBannerColor() {
        return bannerColor;
    }
    public void setBannerColor(String bannerColor) {
        this.bannerColor = bannerColor;
    }

    //TODO- PRODUCT HORIZONTAL =============================================================================================================
    private String header;
    private List<ProductHorizonModel> productHorizonList;
    private String bg_color;
    private List<MyWishlistModel> proH_viewAllList;

    public HomeModel(int type, String header, List<ProductHorizonModel> productHorizonList, String bg_color,List<MyWishlistModel> proH_viewAllList) {//t53,m-25
        this.type = type;
        this.header = header;
        this.productHorizonList = productHorizonList;
        this.bg_color = bg_color;
        this.proH_viewAllList= proH_viewAllList;
    }

    public List<MyWishlistModel> getProH_viewAllList() {
        return proH_viewAllList;
    }

    public void setProH_viewAllList(List<MyWishlistModel> proH_viewAllList) {
        this.proH_viewAllList = proH_viewAllList;
    }

    public HomeModel(int type, String header, List<ProductHorizonModel> productHorizonList, String bg_color) {
        this.type = type;
        this.header = header;
        this.productHorizonList = productHorizonList;
        this.bg_color = bg_color;
    }

    public String getBg_color() {
        return bg_color;
    }
    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public List<ProductHorizonModel> getProductHorizonList() {
        return productHorizonList;
    }
    public void setProductHorizonList(List<ProductHorizonModel> productHorizonList) {
        this.productHorizonList = productHorizonList;
    }

//TODO- PRODUCT GRID =====================================================================================================================

}