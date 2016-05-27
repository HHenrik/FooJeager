package com.example.henrik.googlemapsexample.favourites;

//----------------------------------------IMPORTS-------------------------------------------------\\

//Android imports
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

//Package imports
import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.globalclasses.DatabaseHandler;
import com.example.henrik.googlemapsexample.restaurant.Restaurant;
import com.example.henrik.googlemapsexample.restaurant.RestaurantActivity;
import com.example.henrik.googlemapsexample.review.ReviewDetailed;

//Java imports
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Activity_Favourites extends AppCompatActivity {

//---------------------------------------VARIABLES------------------------------------------------\\
    private ArrayList<Object_Favourite>  favouriteList = new ArrayList();
    private ListAdapter listAdapter;
    private ArrayList<Restaurant> restaurantList = new ArrayList();
    private DatabaseHandler dbHandler = new DatabaseHandler(this);

    private String SAVED_INFO = "storedAccountSettings";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Set<String> markedFavIds;
    private List loadedFavList;
    private int counter = 0;
    private ArrayList<Integer> indexContainer = new ArrayList<>();
    double average;
//---------------------------------------ON-CREATION----------------------------------------------\\
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set layout and create scene
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    public void onResume(){
        super.onResume();
        favouriteList.clear();

        restaurantList = DataStorage.getInstance().getRestaurantList();
        markedFavIds = preferences.getStringSet("markedFavs", new HashSet<String>());
        loadedFavList = new ArrayList(markedFavIds);

        for(int i = 0; i < loadedFavList.size(); i++){
            Log.d("INDEX " + String.valueOf(i), " = " + loadedFavList.get(i));
        }

        for(int i = 0; i < loadedFavList.size(); i++){
            for(int j = 0; j < restaurantList.size(); j++){
                if(loadedFavList.get(i).equals(restaurantList.get(j).getId())){
                    Log.d("HIT INDEX: ", "Loaded: " + String.valueOf(i) + " Restaurant" + String.valueOf(j));


                    dbHandler.getReviewAvarageFromRestaurant(restaurantList.get(j).getId(), new DatabaseHandler.callbackGetReviewAvarageFromRestaurant() {
                        @Override
                        public void onSuccess(double avarage) {
                            average = avarage;
                            Log.d("AVERAGE RATING: ", String.valueOf(average));
                        }
                    });

                    favouriteList.add(new Object_Favourite(i+1, restaurantList.get(j).getId(), restaurantList.get(j).getName(), Float.parseFloat(Double.toString(average))));
                    indexContainer.add(j);
                }

                Log.d("INNER LOOP TURN: ", String.valueOf(j));
            }

            Log.d("OUTER LOOP TURN: ", String.valueOf(i));

        }

        //Instantiate the listview and the custom adapter
        ListView favouritesView = (ListView) findViewById(R.id.listView);
        listAdapter = new Adapter_Favourite(this, favouriteList);

        //Set the adapter for the list
        favouritesView.setAdapter(listAdapter);

        favouritesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(Activity_Favourites.this, RestaurantActivity.class);
                DataStorage.getInstance().setActiveRestaurant(indexContainer.get(position));
                startActivity(intent);
            }
        });
    }
}
