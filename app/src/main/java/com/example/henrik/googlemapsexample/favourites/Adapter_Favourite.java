package com.example.henrik.googlemapsexample.favourites;

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
 * Created by Nicklas on 2016-05-23.
 */
public class Adapter_Favourite extends ArrayAdapter<Object_Favourite> {
    TextView restaurantName;
    RatingBar googleScore;

    public Adapter_Favourite(Context context, ArrayList<Object_Favourite> favList) {
        super(context, R.layout.row_favourite, favList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.row_favourite, parent, false);

        restaurantName = (TextView) customView.findViewById(R.id.restaurantName);
        googleScore = (RatingBar) customView.findViewById(R.id.ratingBar);

        restaurantName.setText(getItem(position).getPosition() + ". " + getItem(position).getName());
        googleScore.setRating(getItem(position).getGoogleRating());

        return customView;
    }
}
