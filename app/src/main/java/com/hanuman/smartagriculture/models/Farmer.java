package com.hanuman.smartagriculture.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Farmer implements Serializable {
    @Exclude
    private String key;

    String mobile,district, municipality,province;

    public Farmer(String mobile, String district, String municipality,String province) {
        this.mobile = mobile;
        this.district = district;
        this.municipality = municipality;
        this.province = province;
    }
    public Farmer(){}

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
