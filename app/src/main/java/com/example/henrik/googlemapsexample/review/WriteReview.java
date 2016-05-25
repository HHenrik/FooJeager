package com.example.henrik.googlemapsexample.review;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.globalclasses.DatabaseHandler;
import com.example.henrik.googlemapsexample.restaurant.RestaurantActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Minkan on 2016-05-23.
 */
public class WriteReview extends AppCompatActivity {

    private DatabaseHandler dbHandler = new DatabaseHandler(this);

    private EditText comment;

    private RatingBar affBar;
    private RatingBar staffBar;
    private RatingBar amBar;
    private RatingBar qBar;

    private int affordability;
    private int staff;
    private int ambience;
    private int quality;
    private String text;
    private String date;
    private String deviceId;
    private String restaurantId;

    private Button send;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review);

        affBar = (RatingBar) findViewById(R.id.affordabilityBar);
        staffBar = (RatingBar) findViewById(R.id.staffBar);
        amBar = (RatingBar) findViewById(R.id.ambienceBar);
        qBar = (RatingBar) findViewById(R.id.qualityBar);

        comment = (EditText) findViewById(R.id.commentText);

        send = (Button) findViewById(R.id.sendReviewButton);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affordability = (int) affBar.getRating();
                staff = (int) staffBar.getRating();
                ambience = (int) amBar.getRating();
                quality = (int) qBar.getRating();

                text = comment.getText().toString();

                date = getDate(System.currentTimeMillis());

                deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                restaurantId = DataStorage.getInstance().getRestaurantList().get(DataStorage.getInstance().getActiveRestaurant()).getId();

                createReview();
            }
        });

    }

    private void createReview(){

        dbHandler.addReview(text, staff, affordability, quality, ambience, date, restaurantId, deviceId);

        Intent intent = new Intent(WriteReview.this, RestaurantActivity.class);

        startActivity(intent);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

}
