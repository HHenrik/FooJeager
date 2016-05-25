package com.example.henrik.googlemapsexample.favourites;

/**
 * Created by Nicklas on 2016-05-25.
 */
public class FavouriteItem {
    private String restaurantName;
    private String restaurantId;

    public FavouriteItem(String restaurantName, String restaurantId) {
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
