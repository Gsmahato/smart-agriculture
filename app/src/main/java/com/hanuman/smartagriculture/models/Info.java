package com.hanuman.smartagriculture.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Info implements Serializable {
    @Exclude
    private String key;
    String infoImage, infoTitle,  infoDetails, infoType;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Info(String infoImage, String infoTitle, String infoDetails, String infoType ){
        if (infoTitle.trim().equals("") && infoDetails.equals("")) {
            infoTitle="No Title";
            infoDetails= "No Details";
        }
        this.infoImage = infoImage;
        this.infoTitle = infoTitle;
        this.infoDetails = infoDetails;
        this.infoType = infoType;
    }
     Info(){ }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getInfoImage() {
        return infoImage;
    }

    public void setInfoImage(String infoImage) {
        this.infoImage = infoImage;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getInfoDetails() {
        return infoDetails;
    }

    public void setInfoDetails(String infoDetails) {
        this.infoDetails = infoDetails;
    }
}
