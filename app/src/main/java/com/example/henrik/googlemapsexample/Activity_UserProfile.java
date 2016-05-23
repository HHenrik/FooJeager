package com.example.henrik.googlemapsexample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Activity_UserProfile extends AppCompatActivity {
    private ArrayList<ReviewObject> recentReviewsList = new ArrayList();
    private ListAdapter adapter;

    private String SAVED_INFO = "storedAccountSettings";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String androidId;
    private boolean firstAppStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        editor = preferences.edit();

        firstAppStart = preferences.getBoolean("firstAppStart", true);

        if(firstAppStart == true){
            androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            createAlertDialog();
        }
        else{
            updateUserName(preferences.getString("userName", "-"));
            updateLikes("80");
            updateDislikes("40");

            int level = (Integer.parseInt("80") - Integer.parseInt("40")) / 10;
            updateLevel(String.valueOf(level));

            setRecentReviews();
        }

    }

    private void createAlertDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder.setCancelable(false)

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        editor.putString("userName", userInput.getText().toString());
                        editor.putString("androidID", androidId);
                        editor.putBoolean("firstAppStart", false);
                        editor.commit();
                        //getDataFromDB
                        updateUserName(preferences.getString("userName", "-"));
                        updateLikes("80");
                        updateDislikes("40");

                        int level = (Integer.parseInt("80") - Integer.parseInt("40")) / 10;
                        updateLevel(String.valueOf(level));

                        setRecentReviews();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                editor.putBoolean("firstAppStart", true);
                                editor.commit();
                                dialog.cancel();
                            }
                        });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F4F4F4"));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#F4F4F4"));
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xFF107896));

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
        recentReviewsList.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Hans Villius", "Mannen i restaurangen vet inte att han snart kommer bli sparkad", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 2, 0, 0, 0, 0, 0, 0, "Kent-Jonas", "Möglig mat men den var ändå ätbar. Otrevlig personal men jag återkommer ändå.", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "Lina Åkesson", "Top notch, fan rätt schysst asså! Äter mer än gärna här igen.", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 4, 0, 0, 0, 0, 0, 0, "G-son", "", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Gördis 47år", "Inte mycket att hänga i granen men helt ok", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "Magic Mike", "https://www.youtube.com/channel/UCPlV0OpQMImKviSTWHJEDi", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Nicklas Brisendal", "Äter hellre på khai mui.", "", "", ""));

        final ListView recentReviews = (ListView) findViewById(R.id.recentReviews);
        adapter = new ReviewAdapter(this, recentReviewsList);

        recentReviews.setAdapter(adapter);
    }

}
