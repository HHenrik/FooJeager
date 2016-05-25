package com.example.henrik.googlemapsexample.favourites;

//----------------------------------------IMPORTS-------------------------------------------------\\

//Android imports
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

//Package imports
import com.example.henrik.googlemapsexample.R;

//Java imports
import java.util.ArrayList;

public class Activity_Favourites extends AppCompatActivity {

//---------------------------------------VARIABLES------------------------------------------------\\
    private ArrayList<Object_Favourite>  favouriteList = new ArrayList();
    private ListAdapter listAdapter;

//---------------------------------------ON-CREATION----------------------------------------------\\
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set layout and create scene
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //Add sample objects to the list
        favouriteList.add(new Object_Favourite(1, "12345", "Burger King", 4));
        favouriteList.add(new Object_Favourite(2, "56789", "Max", 3));
        favouriteList.add(new Object_Favourite(3, "24680", "MackeDonkan", 2));

        //Instantiate the listview and the custom adapter
        ListView favouritesView = (ListView) findViewById(R.id.listView);
        listAdapter = new Adapter_Favourite(this, favouriteList);

        //Set the adapter for the list
        favouritesView.setAdapter(listAdapter);
    }
}
