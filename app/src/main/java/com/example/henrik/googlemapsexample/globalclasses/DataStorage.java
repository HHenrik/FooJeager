package com.example.henrik.googlemapsexample.globalclasses;

import com.example.henrik.googlemapsexample.restaurant.Restaurant;
import com.example.henrik.googlemapsexample.review.ReviewObject;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Henrik on 2016-05-11.
 */
public class DataStorage {
    private static DataStorage dataStorage;
    private ArrayList<Restaurant> restaurantList = new ArrayList();
    private LatLng userPosition;
    private boolean userPositionSupport = false;
    private boolean reviewType;
    private int activeRestaurantIndex;
    private ReviewObject review;
    private boolean fromReview = false;
    private boolean filterIsActive = false;

    private DataStorage() {

    }

    //---------------------GETS INFORMATION FROM DATASTORAGE----------------------\\
    public static DataStorage getInstance() {
        if (dataStorage == null) {
            dataStorage = new DataStorage();
        }

        return dataStorage;
    }

    public ArrayList<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(ArrayList<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public LatLng getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(LatLng userPosition) {
        this.userPosition = userPosition;
    }

    public boolean isUserPositionSupport() {
        return userPositionSupport;
    }

    public void setUserPositionSupport(boolean userPositionSupport) {
        this.userPositionSupport = userPositionSupport;
    }

    public boolean isReviewType() {
        return reviewType;
    }

    public void setReviewType(boolean reviewType) {
        this.reviewType = reviewType;
    }

    public Integer getActiveRestaurant() {
        return activeRestaurantIndex;
    }

    public void setActiveRestaurant(int activeRestaurantIndex) {
        this.activeRestaurantIndex = activeRestaurantIndex;
    }

    public void setReview(ReviewObject review) {
        this.review = review;
    }

    public ReviewObject getReview() {
        return review;
    }

    public boolean isFromReview() {
        return fromReview;
    }

    public void setFromReview(boolean fromReview) {
        this.fromReview = fromReview;
    }

    public boolean isFilterIsActive() {
        return filterIsActive;
    }

    public void setFilterIsActive(boolean filterIsActive) {
        this.filterIsActive = filterIsActive;
    }
}
