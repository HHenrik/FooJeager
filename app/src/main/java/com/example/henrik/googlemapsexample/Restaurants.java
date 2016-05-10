package com.example.henrik.googlemapsexample;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Henrik on 2016-05-06.
 */
public class Restaurants {
    private String position;
    private String googleRating;
    private String open_now;
    private String name;
    private String vicinity;
    private  Marker marker;


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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
