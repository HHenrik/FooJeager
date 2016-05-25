package com.example.henrik.googlemapsexample.favourites;

/**
 * Created by Nicklas on 2016-05-23.
 */
public class Object_Favourite {
    private int position;
    private String restaurantId ;
    private String name;
    private float googleRating;


    public Object_Favourite(int position, String restaurantId, String name, float googleRating) {
        this.setPosition(position);
        this.setRestaurantId(restaurantId);
        this.setName(name);
        this.setGoogleRating(googleRating);
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getGoogleRating() {
        return googleRating;
    }

    public void setGoogleRating(float googleRating) {
        this.googleRating = googleRating;
    }
}
