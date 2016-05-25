package com.example.henrik.googlemapsexample.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.henrik.googlemapsexample.R;

import java.util.ArrayList;

/**
 * Created by Henrik on 2016-05-23.
 */
public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    public RestaurantAdapter(Context context, ArrayList<Restaurant> res) {
        super(context, R.layout.restaurant_row, res);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.restaurant_row, parent, false);

        TextView name = (TextView) customView.findViewById(R.id.restaurantName);
        TextView distance = (TextView) customView.findViewById(R.id.restaurantDistance);
        TextView review = (TextView) customView.findViewById(R.id.restaurantReview);

        name.setText(getItem(position).getName());
        distance.setText("Distance: " + String.valueOf(getItem(position).getDistanceToRestaurant().intValue())+" m");
        if(getItem(position).getGoogleRating().equals("0"))
            review.setText("Not graded");
        else
            review.setText(getItem(position).getGoogleRating().toString());

        return customView;
    }
}
