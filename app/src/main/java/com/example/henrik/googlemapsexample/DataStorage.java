package com.example.henrik.googlemapsexample;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Henrik on 2016-05-11.
 */
public class DataStorage {
    private static DataStorage dataStorage;
    private ArrayList<Restaurants> restaurantsList = new ArrayList();
    private boolean isPlacedLoaded = false;
    private String activeWebLink;
    private LatLng userPostion;
    private boolean userPositionSupport = false;
    private boolean reviewType;
    private String activeRestaurant;
    private DataStorage(){

    }
    //---------------------GETS INFORMATION FROM DATASTORAGE----------------------\\
    public static DataStorage getInstance(){
        if(dataStorage == null){
            dataStorage = new DataStorage();
        }

        return dataStorage;
    }

    public ArrayList<Restaurants> getRestaurantsList() {
        return restaurantsList;
    }

    public void setRestaurantsList(ArrayList<Restaurants> restaurantsList) {
        this.restaurantsList = restaurantsList;
    }

    public boolean isPlacedLoaded() {
        return isPlacedLoaded;
    }

    public void setPlacedLoaded(boolean placedLoaded) {
        isPlacedLoaded = placedLoaded;
    }

    public String getActiveWebLink() {
        return activeWebLink;
    }

    public void setActiveWebLink(String activeWebLink) {
        this.activeWebLink = activeWebLink;
    }

    public LatLng getUserPostion() {
        return userPostion;
    }

    public void setUserPostion(LatLng userPostion) {
        this.userPostion = userPostion;
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

    public String getActiveRestaurant() {
        return activeRestaurant;
    }

    public void setActiveRestaurant(String activeRestaurant) {
        this.activeRestaurant = activeRestaurant;
    }
}
