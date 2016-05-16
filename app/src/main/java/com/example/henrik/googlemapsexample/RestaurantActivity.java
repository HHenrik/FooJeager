package com.example.henrik.googlemapsexample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Henrik on 2016-05-11.
 */


public class RestaurantActivity extends AppCompatActivity {
    private ArrayList <Restaurants> restaurantsList = new ArrayList();
    private String restaurantPhoneNumber;

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
        String restaurantName = "";
        if (bundle != null) {
            restaurantName = bundle.getString("key");
            Log.d(restaurantName,"ss");
            infoView.setText(restaurantName);
            findVaribleOfCertainRestaurant(restaurantName);
        }
    }
    private void findVaribleOfCertainRestaurant(String restName){
        RatingBar googleRating;

        googleRating = (RatingBar) findViewById(R.id.ratingBar);
        for (int i = 0; i < DataStorage.getInstance().getRestaurantsList().size(); i++) {
          //  Log.d(DataStorage.getInstance().getRestaurantsList().get(i).getName(),"Loop");
            if (restaurantsList.get(i).getName().equals(restName)) {
               googleRating.setRating(Float.parseFloat(restaurantsList.get(i).getGoogleRating()));
                //Log.d(DataStorage.getInstance().getRestaurantsList().get(i).getPhoneNumber(),"Git Gud");
                restaurantPhoneNumber = restaurantsList.get(i).getPhoneNumber();
               DataStorage.getInstance().setActiveWebLink(restaurantsList.get(i).getWebsiteLink());
            }
        }}

    public void call(View v){
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + restaurantPhoneNumber));
            startActivity(intent);
        }
        catch(Exception e){
            System.out.println("Call failed" + e);
        }
    }
    private void createYourFragment(Fragment fragmentName){
        if(DataStorage.getInstance().getActiveWebLink()!="null") {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.restaurant_view, fragmentName);
            transaction.commit();
        }
        else {
            //Något meddelande
        }
    }
    public void webPageButton(View v){
        if(restaurantPhoneNumber!=null) {
            Website_fragment fragment = new Website_fragment();
            createYourFragment(fragment);
        }
        else {
            //Något meddelande
        }
    }


}




