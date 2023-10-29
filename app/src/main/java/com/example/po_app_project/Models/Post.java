package com.example.po_app_project.Models;

import android.graphics.Bitmap;

public class Post {
    private int id,likes;
    private String date,description;
    private Bitmap img;
    private User user;
    private boolean selfLike;

    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
    }
    public int getLikes() {
        return likes;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return img;
    }
    public void setImage(Bitmap img) {
        this.img = img;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public boolean isSelfLike() {
        return selfLike;
    }
    public void setSelfLike(boolean selfLike) {
        this.selfLike = selfLike;
    }
}