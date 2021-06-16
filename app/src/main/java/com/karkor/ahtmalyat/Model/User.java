package com.karkor.ahtmalyat.Model;

public class User {
    private String id;
    private String name;
    private String email;
    private String mobileNum;
    private String passwrod;
    private String enable;
    private String imageUrl;
    private String token;
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getEnable() {
        return enable;
    }
    public void setEnable(String enable) {
        this.enable = enable;
    }
    public String getpasswrod() {
        return passwrod;
    }
    public void setpasswrod(String passwrod) {
        this.passwrod = passwrod;
    }
    public String getMobileNum() {
        return mobileNum;
    }
    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String userEmail) {
        this.email = userEmail;
    }


    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String ImageUrl) {
        this.imageUrl = ImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

