package com.hanuman.smartagriculture.models;

public class Users {
    String userId, profilePic,userName,email, address, latitude, longitude;

    public Users(String userId, String profilePic, String userName, String email, String longitude,String latitude, String address) {
        this.userId = userId;
        this.profilePic = profilePic;
        this.userName = userName;
        this.email = email;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Users(){}

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
