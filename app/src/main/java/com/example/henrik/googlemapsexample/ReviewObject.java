package com.example.henrik.googlemapsexample;

/**
 * Created by Minkan on 2016-05-16.
 */
public class ReviewObject {

    private float score;
    private String user;
    private String text;


    public ReviewObject(float score, String user, String text) {
        this.score = score;
        this.user = user;
        this.text = text;
    }

    public float getScore() {
        return score;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }
}
