package com.example.henrik.googlemapsexample.favourites;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.review.ReviewAdapter;
import com.example.henrik.googlemapsexample.review.ReviewObject;

import java.util.ArrayList;

public class Activity_Favourites extends AppCompatActivity {
    private ArrayList<Object_Favourite>  favouriteList = new ArrayList();
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        favouriteList.add(new Object_Favourite(1, "12345", "Burger King", 4));
        favouriteList.add(new Object_Favourite(2, "56789", "Max", 3));
        favouriteList.add(new Object_Favourite(3, "24680", "MackeDonkan", 2));

        ListView favouritesView = (ListView) findViewById(R.id.listView);
        listAdapter = new Adapter_Favourite(this, favouriteList);

        favouritesView.setAdapter(listAdapter);
    }
}
