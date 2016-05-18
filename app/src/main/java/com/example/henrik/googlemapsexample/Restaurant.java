package com.example.henrik.googlemapsexample;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Henrik on 2016-05-06.
 */
public class Restaurant {
    private LatLng position;
    private String googleRating;
    private String open_now;
    private String name;
    private String vicinity;
    private Marker marker;
    private String phoneNumber;
    private String id;
    private String websiteLink;
    private String priceLevel;
    private ArrayList <ReviewObject> reviews = new ArrayList();
    private String[] openHoursArray;
    private boolean openNow;



    public Marker getMarker() {
        return marker;
    }

    public void setMarker( Marker marker) {
        this.marker = marker;
    }

    //Byt till constructor för saker som bara ska sättas en gång

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String isOpen_now() {
        return open_now;
    }

    public void setOpen_now(String open_now) {
        this.open_now = open_now;
    }

    public String getGoogleRating() {
        return googleRating;
    }

    public void setGoogleRating(String googleRating) {
        this.googleRating = googleRating;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }


    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(String priceLevel) {
        this.priceLevel = priceLevel;
    }

    public ArrayList getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList reviews) {
        this.reviews = reviews;
    }

    public String[] getOpenHoursArray() {
        return openHoursArray;
    }

    public void setOpenHoursArray(String[] openHoursArray) {
        this.openHoursArray = openHoursArray;
    }

    public boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }
}
