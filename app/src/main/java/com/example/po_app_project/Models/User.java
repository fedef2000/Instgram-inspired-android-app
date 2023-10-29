package com.example.po_app_project.Models;

import android.graphics.Bitmap;

public class User {
    private int id;
    private String username;
    Bitmap profileImg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getPhoto() {
        return profileImg;
    }

    public void setPhoto(Bitmap profileImg) {
        this.profileImg = profileImg;
    }
}