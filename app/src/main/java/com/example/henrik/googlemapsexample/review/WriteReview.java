package com.example.henrik.googlemapsexample.review;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.globalclasses.DatabaseHandler;
import com.example.henrik.googlemapsexample.restaurant.RestaurantActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Minkan on 2016-05-23.
 */
public class WriteReview extends AppCompatActivity {

    private DatabaseHandler dbHandler = new DatabaseHandler(this);

    private EditText comment;

    private RatingBar affBar;
    private RatingBar staffBar;
    private RatingBar amBar;
    private RatingBar qBar;

    private int affordability;
    private int staff;
    private int ambience;
    private int quality;
    private String text;
    private String date;
    private String deviceId;
    private String restaurantId;

    private Button send;

    private String SAVED_INFO = "storedAccountSettings";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String androidId;
    private boolean firstAppStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review);

        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        editor = preferences.edit();
        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //Check in the stored preferences if it is the first time the application starts on this phone
        firstAppStart = preferences.getBoolean("firstAppStart", true);

        if(firstAppStart == true){
            //If it is the first time do the following:

            //Create a pop-up dialog that prompts the user to enter a username
            createAlertDialog();
        }

            affBar = (RatingBar) findViewById(R.id.affordabilityBar);
            staffBar = (RatingBar) findViewById(R.id.staffBar);
            amBar = (RatingBar) findViewById(R.id.ambienceBar);
            qBar = (RatingBar) findViewById(R.id.qualityBar);

            comment = (EditText) findViewById(R.id.commentText);

            send = (Button) findViewById(R.id.sendReviewButton);

            send.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    affordability = (int) affBar.getRating();
                    staff = (int) staffBar.getRating();
                    ambience = (int) amBar.getRating();
                    quality = (int) qBar.getRating();

                    text = comment.getText().toString();

                    date = getDate(System.currentTimeMillis());

                    deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                    restaurantId = DataStorage.getInstance().getRestaurantList().get(DataStorage.getInstance().getActiveRestaurant()).getId();

                    createReview();
                }
            });

    }

    private void createReview(){

        dbHandler.addReview(text, staff, affordability, quality, ambience, date, restaurantId, deviceId);

        finish();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private void createAlertDialog(){
        //get the XML view for the alertdialog
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);

        //Instantiate the alertdialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //Set the layout of the dialogbox to the xml file
        alertDialogBuilder.setView(promptsView);

        //Instantiate the editText field that will be used for the users input
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        //Set dialog buttons
        alertDialogBuilder.setCancelable(false)
                //If the OK button is pressed:
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //Set it to that the application has been started once on this device and save changes to the preferences
                        editor.putBoolean("firstAppStart", false);
                        editor.commit();

                        createUser(androidId, userInput.getText().toString());
                    }
                })

                //If the cancel button is pressed
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //Set it to that the application has never been run on this device before and save the changes
                                editor.putBoolean("firstAppStart", true);
                                editor.commit();

                                //Shut down the dialog
                                dialog.cancel();
                                finish();
                            }
                        });


        //Create the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        //Display the dialog
        alertDialog.show();

        //Change the color of the text on the buttons as well as the background color of the dialog.
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F4F4F4"));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#F4F4F4"));
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xFF107896));

    }

    private void createUser(final String deviceId, final String userName){
        dbHandler.addUser(deviceId, userName, new DatabaseHandler.callbackAddUserComplete() {
            @Override
            public void onSuccess(String complete) {
                Log.d("User was created: ", "Device id: "+ deviceId + " Username: " + userName + complete);
            }
        });

    }


}
