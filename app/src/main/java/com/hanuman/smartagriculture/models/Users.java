package com.hanuman.smartagriculture.models;

public class Users {
    String userId, profilePic,userName,email;

    public Users(String userId, String profilePic, String userName, String email) {
        this.userId = userId;
        this.profilePic = profilePic;
        this.userName = userName;
        this.email = email;
    }
    public Users(){}

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
