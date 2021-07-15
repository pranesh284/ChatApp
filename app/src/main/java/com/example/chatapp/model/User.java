package com.example.chatapp.model;


public class User {
    private String id;
    private String username;
    private String imageUrl;
    private String status;
    private String search;


    public User(){ }

    public User(String id, String userName, String imageUrl,String status,String search) {
        this.id = id;
        this.username = userName;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
