package com.example.henrik.googlemapsexample.review;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.henrik.googlemapsexample.R;

import java.util.ArrayList;

/**
 * Created by Minkan on 2016-05-16.
 */
public class ReviewAdapter extends ArrayAdapter<ReviewObject> {

    public ReviewAdapter(Context context, ArrayList<ReviewObject> rew) {
        super(context, R.layout.review_row, rew);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.review_row, parent, false);

        TextView user = (TextView) customView.findViewById(R.id.userName);
        TextView text = (TextView) customView.findViewById(R.id.text);
        RatingBar score = (RatingBar) customView.findViewById(R.id.ratingBar);

        //user.setText(getItem(position).getUser());
        text.setText(getItem(position).getText());
        score.setRating(getItem(position).getAverageScore());

        return customView;
    }
}
