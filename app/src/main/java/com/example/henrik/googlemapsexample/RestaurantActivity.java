package com.example.henrik.googlemapsexample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
    private ArrayList<Restaurant> restaurantList = new ArrayList();
    private int restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant);
        restaurantList = DataStorage.getInstance().getRestaurantList();
        TextView infoView = (TextView) findViewById(R.id.infoView);
        Context context = getApplicationContext();
        restaurantID = DataStorage.getInstance().getActiveRestaurant();
        findVaribleOfCertainRestaurant(infoView);
    }

    private void findVaribleOfCertainRestaurant(TextView infoView) {
        RatingBar googleRating;
        infoView.setText(restaurantList.get(restaurantID).getName());
        googleRating = (RatingBar) findViewById(R.id.ratingBar);
        googleRating.setRating(Float.parseFloat(restaurantList.get(restaurantID).getGoogleRating()));


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

    public void call(View v) {
        if (restaurantList.get(restaurantID).getPhoneNumber() != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + restaurantList.get(restaurantID).getPhoneNumber()));
                startActivity(intent);
            } catch (Exception e) {
                System.out.println("Call failed" + e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Restaurant has no phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private void createYourFragment(Fragment fragmentName) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.restaurant_view, fragmentName);
        transaction.commit();
    }

    public void webPageButton(View v) {
        if (restaurantList.get(restaurantID).getWebsiteLink() != null) {
            Website_fragment fragment = new Website_fragment();
            createYourFragment(fragment);
        } else {
            Toast.makeText(getApplicationContext(), "Restaurant has no website", Toast.LENGTH_SHORT).show();
        }
    }

    public void gpsButton(View v) {
        LatLng userPosition;
        if (DataStorage.getInstance().isUserPositionSupport()) {
            userPosition = DataStorage.getInstance().getUserPosition();
        } else {
            userPosition = new LatLng(56.0333333, 14.1333333);  //Kordinater för Kristianstad. Om det ingen user position.
        }
        String googleDirectionURL = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", userPosition.latitude, userPosition.longitude, "Your Position", restaurantList.get(restaurantID).getPosition().latitude, restaurantList.get(restaurantID).getPosition().longitude, restaurantList.get(restaurantID).getName());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleDirectionURL));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }


}




