package com.example.henrik.googlemapsexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MenuAdapter extends ArrayAdapter<MenuObject> {

    public MenuAdapter(Context context, ArrayList<MenuObject> men) {
        super(context, R.layout.menu_row, men);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.menu_row, parent, false);

        TextView title = (TextView) customView.findViewById(R.id.titleView);
        TextView ingredients = (TextView) customView.findViewById(R.id.ingView);
        TextView price = (TextView) customView.findViewById(R.id.priceView);


        title.setText(getItem(position).getTitle());
        ingredients.setText(getItem(position).getIngredients());
        price.setText(getItem(position).getPrice());

        return customView;
    }
}
