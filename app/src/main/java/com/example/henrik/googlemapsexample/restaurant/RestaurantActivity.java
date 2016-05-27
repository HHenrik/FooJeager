package com.example.henrik.googlemapsexample.restaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.fragments.Website_fragment;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.review.ReviewViewer;
import com.example.henrik.googlemapsexample.review.WriteReview;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Henrik on 2016-05-11.
 */


public class RestaurantActivity extends AppCompatActivity {
    private ArrayList<Restaurant> restaurantList = new ArrayList();
    private int restaurantID;
    private ToggleButton favouriteMarked;

    private String SAVED_INFO = "storedAccountSettings";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Set<String> markedFavIds;
    private int storedAtIndex = -1;
    private List loadedFavList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant);
        restaurantList = DataStorage.getInstance().getRestaurantList();
        TextView infoView = (TextView) findViewById(R.id.infoView);
        Context context = getApplicationContext();
        restaurantID = DataStorage.getInstance().getActiveRestaurant();
        findVaribleOfCertainRestaurant(infoView);
        showRestaurantStatusPicture();

        favouriteMarked = (ToggleButton) findViewById(R.id.favouriteButton);
        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        editor = preferences.edit();

        //Read favourites from sharedpreferences
        //Set button toggle if id matches the restaurants id

            //editor.clear();
           //editor.commit();
            //Log.d("CLEARED", " NOW");

        markedFavIds = preferences.getStringSet("markedFavs", new HashSet<String>());
        loadedFavList = new ArrayList(markedFavIds);

        for(int i = 0; i < loadedFavList.size(); i++) {
            if (restaurantList.get(restaurantID).getId().equals(loadedFavList.get(i))){
                Log.d("FAVOURITE FOUND: ", "AT INDEX " + i);
                storedAtIndex = i;
                Log.d("STORED INDEX = ", String.valueOf(storedAtIndex));
                Log.d("FOUND: ", loadedFavList.get(i) + " was " + restaurantList.get(restaurantID).getId());
                favouriteMarked.setChecked(true);
                break;
            }
        }

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
        transaction.addToBackStack("tag").commit();
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

    public void reviewButton(View v) {
        Intent intent = new Intent(RestaurantActivity.this, WriteReview.class);

        startActivity(intent);
    }

    public void openingTimesButton(View v) {
        if (restaurantList.get(restaurantID).getOpenHoursArray() != null) {
            Log.d("clicked", "has");
            RestaurantOpeningTimes_fragment fragment = new RestaurantOpeningTimes_fragment();
            createYourFragment(fragment);
        } else {
            //Dölj knappen eller toast
        }

    }

    private void showRestaurantStatusPicture() {
        ImageView openNowStatus;
        openNowStatus = (ImageView) findViewById(R.id.openNow);
        if (restaurantList.get(restaurantID).getOpenNow()) {
            //openNowStatus.setImageResource(R.drawable.open);
            Log.d("Open Satuts true",String.valueOf(restaurantList.get(restaurantID).getOpenNow()));
            Log.d(restaurantList.get(restaurantID).getName(),String.valueOf(restaurantList.get(restaurantID).getOpenNow()));
        } else {
            //openNowStatus.setImageResource(R.drawable.closed);
            Log.d("Open Satuts false",String.valueOf(restaurantList.get(restaurantID).getOpenNow()));
            Log.d(restaurantList.get(restaurantID).getName(),String.valueOf(restaurantList.get(restaurantID).getOpenNow()));
        }

    }

    public void onFavouriteClick(View view) {
        String restName = restaurantList.get(restaurantID).getName();
        String restId = restaurantList.get(restaurantID).getId();

        if (favouriteMarked.isChecked()) {
            Log.d("AS FAVOURITE: ", restName);
            Log.d("AS FAVOURITE: ", restId);

            markedFavIds.add(restId);

            editor.remove("markedFavs");
            editor.commit();

            editor.putStringSet("markedFavs", markedFavIds);
            editor.commit();

            Log.d("ADDING....: ", "COMPLETE!");

            Set<String> readFavs = preferences.getStringSet("markedFavs", null);
            List list = new ArrayList(readFavs);
            Log.d("PRINT: ", list.toString());

        } else {
            Log.d("RESTAURANT INFO: ", restName);
            Log.d("RESTAURANT INFO: ", restId);

            editor.remove("markedFavs");
            editor.commit();

            loadedFavList.remove(storedAtIndex);
            Set setToAdd = new HashSet(loadedFavList);

            editor.putStringSet("markedFavs", setToAdd);
            editor.commit();
            Log.d("REMOVED INDEX: ", String.valueOf(storedAtIndex));

        }



        //Check if button is marked or not

        //If not marked:
        //Add Name and id to file
        //Append to file
        //If marked:
        //READ FULL FILE
        //Remove name and id from file
        //Write file

    }

    private void storeFavourites() {

    }

    private String readFavourites() {
        return "";
    }

}




