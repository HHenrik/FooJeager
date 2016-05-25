package com.example.henrik.googlemapsexample.userprofile;

/**
 * Created by Nicklas on 2016-05-23.
 */
public class User {

    private String device_id;
    private String name;
    private int likes;
    private int dislikes;

    public User(String device_id, String name, int likes, int dislikes) {
        this.device_id = device_id;
        this.name = name;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
