package com.example.henrik.googlemapsexample;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Activity_UserProfile extends AppCompatActivity {
    ArrayList<ReviewObject> recentReviewsList = new ArrayList();
    ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        updateUserName(androidId);
        updateLikes("100");
        updateDislikes("50");
        updateLevel("50");
        setRecentReviews();

    }

    public void updateUserName(String newUser){
        TextView username = (TextView) findViewById(R.id.userLabel);
        username.setText("User:" + newUser);
    }

    public void updateLikes(String likes){
        TextView likesDisplay = (TextView) findViewById(R.id.likesAmountLabel);
        likesDisplay.setText(likes);
    }

    public void updateDislikes(String dislikes){
        TextView dislikesDisplay = (TextView) findViewById(R.id.dislikesAmountLabel);
        dislikesDisplay.setText(dislikes);
    }

    public void updateLevel(String level){
        TextView levelDisplay = (TextView) findViewById(R.id.levelLabel);
        levelDisplay.setText("Lv. " + level);
    }

    public void setRecentReviews(){
        recentReviewsList.add(new ReviewObject(3, "Armando", "Mannen i restaurangen vet inte att han snart kommer bli sparkad"));
        recentReviewsList.add(new ReviewObject(2, "Armando", "Möglig mat men den var ändå ätbar. Otrevlig personal men jag återkommer ändå."));
        recentReviewsList.add(new ReviewObject(5, "Armando", "Top notch, fan rätt schysst asså! Äter mer än gärna här igen."));
        recentReviewsList.add(new ReviewObject(4, "Armando", ""));
        recentReviewsList.add(new ReviewObject(3, "Armando", "Inte mycket att hänga i granen men helt ok"));
        recentReviewsList.add(new ReviewObject(5, "Armando", "https://www.youtube.com/channel/UCPlV0OpQMImKviSTWHJEDi"));
        recentReviewsList.add(new ReviewObject(3, "Armando", "Äter hellre på khai mui."));

        final ListView recentReviews = (ListView) findViewById(R.id.recentReviews);
        adapter = new ReviewAdapter(this, recentReviewsList);

        recentReviews.setAdapter(adapter);
    }
}
