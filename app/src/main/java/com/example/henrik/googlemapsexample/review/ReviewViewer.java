package com.example.henrik.googlemapsexample.review;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DatabaseHandler;
import com.example.henrik.googlemapsexample.userprofile.User;

import java.util.ArrayList;

/**
 * Created by Minkan on 2016-05-16.
 */
public class ReviewViewer extends AppCompatActivity {

    private ArrayList<ReviewObject> list = new ArrayList();

    private ListView rew;

    private ListAdapter adapter;
    private String restaurantId;
    private Boolean googleReview = false;

    private DatabaseHandler dbHandler = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews);
        rew = (ListView) findViewById(R.id.listView);
        restaurantId = DataStorage.getInstance().getRestaurantList().get(DataStorage.getInstance().getActiveRestaurant()).getId();

        if(DataStorage.getInstance().isReviewType()) {
            googleReview = false;
            /*list.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Mannen i restaurangen vet inte att han snart kommer bli sparkad", "", "", ""));
            list.add(new ReviewObject(0, 2, 0, 0, 0, 0, 0, 0, "Möglig mat men den var ändå ätbar. Otrevlig personal men jag återkommer ändå.", "", "", ""));
            list.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "Top notch, fan rätt schysst asså! Äter mer än gärna här igen.", "", "", ""));
            list.add(new ReviewObject(0, 4, 0, 0, 0, 0, 0, 0, "", "", "", ""));
            list.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Inte mycket att hänga i granen men helt ok", "", "", ""));
            list.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "https://www.youtube.com/channel/UCPlV0OpQMImKviSTWHJEDi", "", "", ""));
            list.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Äter hellre på khai mui.", "", "", ""));*/

            getRestaurantReviews(restaurantId);
        }
        else{
            googleReview = true;
            list = DataStorage.getInstance().getRestaurantList().get(DataStorage.getInstance().getActiveRestaurant()).getReviews();
            displayReviews();
        }
    }

    private void getRestaurantReviews(String restaurantId){

        dbHandler.getAllReviewsFromRestaurant(restaurantId, new DatabaseHandler.callbackGetAllReviewsFromRestaurant() {
            @Override
            public void onSuccess(ArrayList<ReviewObject> reviewList) {

                //list = reviewList;
                for(int i = 0; i < reviewList.size(); i++) {
                    list.add(reviewList.get(i));
                }
                /*System.out.println("skit" + list.size());
                for(int i = 0; i < list.size(); i++) {f
                    list2.add(list.get(i));
                }*/
                //list.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "Äter hellre på khai mui.", "", "", ""));
                displayReviews();
            }

        });

    }

    private void displayReviews(){

        adapter = new ReviewAdapter(this, list);

        rew.setAdapter(adapter);

        rew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(googleReview == false) {

                    Intent intent = new Intent(ReviewViewer.this, ReviewDetailed.class);
                    DataStorage.getInstance().setReview(list.get(position));
                    startActivity(intent);
                } else {

                }
            }
        });
    }
}
