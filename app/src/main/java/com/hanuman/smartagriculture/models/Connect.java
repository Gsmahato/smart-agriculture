package com.hanuman.smartagriculture.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Connect implements Serializable {
    @Exclude
    private String key;
    String message,username;

    public Connect(String message,String username) {
        this.message = message;
        this.username = username;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
