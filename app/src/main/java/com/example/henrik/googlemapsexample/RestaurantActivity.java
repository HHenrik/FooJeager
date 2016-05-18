package com.example.henrik.googlemapsexample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Henrik on 2016-05-11.
 */


public class RestaurantActivity extends AppCompatActivity {
    private ArrayList <Restaurants> restaurantsList = new ArrayList();
    private String restaurantPhoneNumber;
    private LatLng restaurantPosition;
    private String restaurantName;

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
        restaurantName = "";
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
                restaurantPosition = restaurantsList.get(i).getPosition();
                //Log.d(DataStorage.getInstance().getRestaurantsList().get(i).getPhoneNumber(),"Git Gud");
                restaurantPhoneNumber = restaurantsList.get(i).getPhoneNumber();
                DataStorage.getInstance().setActiveWebLink(restaurantsList.get(i).getWebsiteLink());
            }
        }



            RatingBar appUserReviews;
            appUserReviews = (RatingBar) findViewById(R.id.appUserReviews);
            appUserReviews.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    DataStorage.getInstance().setReviewType(true);
                    int action = event.getActionMasked();
                    if (action == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(RestaurantActivity.this, ReviewViewer.class);
                        startActivity(intent);
                    }
                    return true;
                }
            });

        googleRating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DataStorage.getInstance().setReviewType(false);
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(RestaurantActivity.this, ReviewViewer.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        }

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
        if(DataStorage.getInstance().getActiveWebLink()!=null) {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.restaurant_view, fragmentName);
            transaction.commit();
        }
        else {
            Toast.makeText(getApplicationContext(), "Restaurant has no phone number", Toast.LENGTH_SHORT).show();
        }
    }
    public void webPageButton(View v){
        if(restaurantPhoneNumber!=null) {
            Website_fragment fragment = new Website_fragment();
            createYourFragment(fragment);
        }
        else {
            Toast.makeText(getApplicationContext(), "Restaurant has no website", Toast.LENGTH_SHORT).show();
        }
    }
    public void gpsButton(View v){
        LatLng userPosition;
        if(DataStorage.getInstance().isUserPositionSupport()){
            userPosition = DataStorage.getInstance().getUserPostion();
        }
        else{
            userPosition = new LatLng(56.0333333,14.1333333);  //Kordinater för Kristianstad. Om det ingen user position.
        }
        String googleDirectionURL = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", userPosition.latitude, userPosition.longitude, "Your Position", restaurantPosition.latitude, restaurantPosition.longitude, restaurantName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleDirectionURL));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }


}




