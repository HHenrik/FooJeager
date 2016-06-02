package com.example.henrik.googlemapsexample.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.globalclasses.DatabaseHandler;
import com.example.henrik.googlemapsexample.userprofile.Activity_UserProfile;
import com.example.henrik.googlemapsexample.userprofile.User;

import java.util.ArrayList;

/**
 * Created by Minkan on 2016-05-22.
 */
public class ReviewDetailed extends AppCompatActivity{

    private ReviewObject rev = new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Mannen i restaurangen vet inte att han snart kommer bli sparkad", "", "", "");
    private ArrayList<ReviewObject> list = new ArrayList();
    private int pos = 1;

    private DatabaseHandler dbHandler = new DatabaseHandler(this);

    private TextView userName;
    private TextView likes;
    private TextView dislikes;
    private TextView review;

    private Button likeButton;
    private Button dislikeButton;

    private RatingBar affBar;
    private RatingBar staffBar;
    private RatingBar amBar;
    private RatingBar qBar;
    private RatingBar averageBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_detail);

        userName = (TextView) findViewById(R.id.userName);
        likes = (TextView) findViewById(R.id.likeValue);
        dislikes = (TextView) findViewById(R.id.dislikeValue);
        review = (TextView) findViewById(R.id.reviewText);

        likeButton = (Button) findViewById(R.id.likeButton);
        dislikeButton = (Button) findViewById(R.id.dislikeButton);

        affBar = (RatingBar) findViewById(R.id.affBar);
        staffBar = (RatingBar) findViewById(R.id.staffBar);
        amBar = (RatingBar) findViewById(R.id.amBar);
        qBar = (RatingBar) findViewById(R.id.qBar);
        averageBar = (RatingBar) findViewById(R.id.averageBar);



        rev = DataStorage.getInstance().getReview();

        getUser(rev.getDeviceId());
        postResults();

        userName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewDetailed.this, Activity_UserProfile.class);
                Log.d("FROM REVIEW: ", rev.getDeviceId());
                intent.putExtra("androidID", rev.getDeviceId());
                DataStorage.getInstance().setFromReview(true);
                startActivity(intent);
            }
        });
/*
        likeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dbHandler.addLikeorDislike( rev.getDeviceId(), rev.getReviewId(), 1);
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dbHandler.addLikeorDislike( rev.getDeviceId(), rev.getReviewId(), 0);
            }
        });*/
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.likeButton:
                dbHandler.addLikeorDislike( rev.getDeviceId(), rev.getReviewId(), 1);
                Log.d("LIKE", " NAOW");
                break;
            case R.id.dislikeButton:
                Log.d("DISLIKE", " NAOW");
                dbHandler.addLikeorDislike( rev.getDeviceId(), rev.getReviewId(), 0);
                break;

        }
        //finish();
    }

    private void postResults(){

        //userName.setText(rev.getUser());
        likes.setText(String.valueOf(rev.getLike()));
        dislikes.setText(String.valueOf(rev.getDislike()));
        review.setText(rev.getText());
        affBar.setRating((float)rev.getAffordabilityScore());
        staffBar.setRating((float)rev.getStaffScore());
        amBar.setRating((float)rev.getAmbienceScore());
        qBar.setRating((float)rev.getQualityScore());
        averageBar.setRating(rev.getAverageScore());
    }

    private void userClicked(){
        Intent intent = new Intent(ReviewDetailed.this, Activity_UserProfile.class);
        startActivity(intent);
    }

    private void getUser(String deviceId){
        dbHandler.getUserData(deviceId, new DatabaseHandler.callbackGetUserData() {
            @Override
            public void onSuccess(User user) {
                Log.d("User: ", "   Device_id: " + user.getDevice_id() + "   Name: " + user.getName() + "   Likes: " + user.getLikes() + "   Dislikes: " + user.getDislikes());
                userName.setText(user.getName());

            }
        });
    }
}
