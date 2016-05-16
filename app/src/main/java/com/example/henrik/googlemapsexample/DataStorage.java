package com.example.henrik.googlemapsexample;

import java.util.ArrayList;

/**
 * Created by Henrik on 2016-05-11.
 */
public class DataStorage {
    private static DataStorage dataStorage;
    private ArrayList<Restaurants> restaurantsList = new ArrayList();
    private boolean isPlacedLoaded = false;
    private String activeWebLink;
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
}
