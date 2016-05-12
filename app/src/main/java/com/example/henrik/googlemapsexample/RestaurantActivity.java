package com.example.henrik.googlemapsexample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Henrik on 2016-05-11.
 */


public class RestaurantActivity extends AppCompatActivity {
    private ArrayList <Restaurants> restaurantsList = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant);
        restaurantsList = DataStorage.getInstance().getRestaurantsList();
        TextView infoView = (TextView)findViewById(R.id.infoView);
        Context context = getApplicationContext();
        setDataOnScreen(context,infoView);

    }
    private void setDataOnScreen(Context context, TextView infoView) {
        Bundle bundle = getIntent().getExtras();
        String restName = "";
        if (bundle != null) {
            restName = bundle.getString("key");
            Log.d(restName,"ss");
            infoView.setText(restName);
            findVaribleOfCertainRestaurant(restName);
        }
    }
    private void findVaribleOfCertainRestaurant(String restName){
        RatingBar googleRating;

        googleRating = (RatingBar) findViewById(R.id.ratingBar);
        for (int i = 0; i < DataStorage.getInstance().getRestaurantsList().size(); i++) {
            Log.d(DataStorage.getInstance().getRestaurantsList().get(i).getName(),"Loop");
            if (restaurantsList.get(i).getName().equals(restName)) {
               googleRating.setRating(Float.parseFloat(restaurantsList.get(i).getGoogleRating()));
                //Log.d(DataStorage.getInstance().getRestaurantsList().get(i).getPhoneNumber(),"Git Gud");

            }
        }}


}




