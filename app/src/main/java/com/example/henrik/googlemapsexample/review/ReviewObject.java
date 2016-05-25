package com.example.henrik.googlemapsexample.review;

/**
 * Created by Minkan on 2016-05-16.
 */
public class ReviewObject {
    private int reviewId;
    private float averageScore;
    private int staffScore;
    private int affordabilityScore;
    private int ambienceScore;
    private int qualityScore;
    private int dislike;
    private int like;
    private String user;
    private String text;
    private String date;
    private String deviceId;
    private String restaurantId;


    public ReviewObject(int reviewId, float averageScore, int staffScore, int affordabilityScore, int ambienceScore, int qualityScore, int dislike, int like, String text, String date, String restaurantId, String deviceId) {
        this.reviewId = reviewId;
        this.averageScore = averageScore;
        this.staffScore = staffScore;
        this.affordabilityScore = affordabilityScore;
        this.ambienceScore = ambienceScore;
        this.qualityScore = qualityScore;
        this.dislike = dislike;
        this.like = like;
        this.text = text;
        this.date = date;
        this.restaurantId = restaurantId;
        this.deviceId = deviceId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public float getAverageScore() {
        return averageScore;
    }

    public int getStaffScore() {
        return staffScore;
    }

    public int getAffordabilityScore() {
        return affordabilityScore;
    }

    public int getAmbienceScore() {
        return ambienceScore;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public int getDislike() {
        return dislike;
    }

    public int getLike() {
        return like;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }


}
