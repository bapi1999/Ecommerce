package com.sbdevs.ecommerce.models;

public class AddressModel {

    private Boolean selected;
    private String city;
    private String locality;
    private String flat_NOorName;
    private String pincode;
    private String landmark;
    private String fullName;
    private String mobileNO;
    private String alternateMobNo;
    private String state;

    public AddressModel(Boolean selected, String city, String locality, String flat_NOorName, String pincode, String landmark, String fullName, String mobileNO, String alternateMobNo, String state) {
        this.selected = selected;
        this.city = city;
        this.locality = locality;
        this.flat_NOorName = flat_NOorName;
        this.pincode = pincode;
        this.landmark = landmark;
        this.fullName = fullName;
        this.mobileNO = mobileNO;
        this.alternateMobNo = alternateMobNo;
        this.state = state;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlat_NOorName() {
        return flat_NOorName;
    }

    public void setFlat_NOorName(String flat_NOorName) {
        this.flat_NOorName = flat_NOorName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNO() {
        return mobileNO;
    }

    public void setMobileNO(String mobileNO) {
        this.mobileNO = mobileNO;
    }

    public String getAlternateMobNo() {
        return alternateMobNo;
    }

    public void setAlternateMobNo(String alternateMobNo) {
        this.alternateMobNo = alternateMobNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
