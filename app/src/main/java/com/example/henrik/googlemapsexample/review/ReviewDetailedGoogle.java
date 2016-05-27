package com.example.henrik.googlemapsexample.review;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;

/**
 * Created by Minkan on 2016-05-26.
 */
public class ReviewDetailedGoogle extends AppCompatActivity {

    TextView userName;
    TextView comment;

    RatingBar averageBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_detail_google);

        comment = (TextView) findViewById(R.id.commentText);
        userName = (TextView) findViewById(R.id.userName);
        averageBar = (RatingBar) findViewById(R.id.ratingBar2);

        ReviewObject rev = DataStorage.getInstance().getReview();

        averageBar.setRating(rev.getAverageScore());
        comment.setText(rev.getText());
        userName.setText(rev.getUser());
    }
}
