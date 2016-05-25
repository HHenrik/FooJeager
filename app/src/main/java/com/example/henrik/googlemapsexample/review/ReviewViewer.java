package com.example.henrik.googlemapsexample.review;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;

import java.util.ArrayList;

/**
 * Created by Minkan on 2016-05-16.
 */
public class ReviewViewer extends AppCompatActivity {

    ArrayList<ReviewObject> list = new ArrayList();

    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews);
        if(DataStorage.getInstance().isReviewType()) {
            list.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Mannen i restaurangen vet inte att han snart kommer bli sparkad", "", "", ""));
            list.add(new ReviewObject(0, 2, 0, 0, 0, 0, 0, 0, "Möglig mat men den var ändå ätbar. Otrevlig personal men jag återkommer ändå.", "", "", ""));
            list.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "Top notch, fan rätt schysst asså! Äter mer än gärna här igen.", "", "", ""));
            list.add(new ReviewObject(0, 4, 0, 0, 0, 0, 0, 0, "", "", "", ""));
            list.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Inte mycket att hänga i granen men helt ok", "", "", ""));
            list.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "https://www.youtube.com/channel/UCPlV0OpQMImKviSTWHJEDi", "", "", ""));
            list.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Äter hellre på khai mui.", "", "", ""));
        }
        else{
                list = DataStorage.getInstance().getRestaurantList().get(DataStorage.getInstance().getActiveRestaurant()).getReviews();
        }





        final ListView rew = (ListView) findViewById(R.id.listView);
        adapter = new ReviewAdapter(this, list);

        rew.setAdapter(adapter);
/*
        rew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ReviewViewer.this, ReviewDetailed.class);
                startActivity(intent);
            }
        });
*/
    }

    /*private void fillList(){
        //hämta från databas och fyll lista
        for(int i = 0; i < size; i++){
            list.add(new ReviewObject(ReviewObject.getScore(i), ReviewObject.getUser(i), ReviewObject.getText(i)));
        }
    }*/

}
