package com.example.henrik.googlemapsexample.userprofile;

//----------------------------------------IMPORTS-------------------------------------------------\\

//Android imports
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

//Package imports
import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.globalclasses.DatabaseHandler;
import com.example.henrik.googlemapsexample.review.ReviewAdapter;
import com.example.henrik.googlemapsexample.review.ReviewObject;

//Java imports
import java.util.ArrayList;

public class Activity_UserProfile extends AppCompatActivity {

//---------------------------------------VARIABLES------------------------------------------------\\
    private ArrayList<ReviewObject> recentReviewsList = new ArrayList();
    private ListAdapter adapter;

    private String SAVED_INFO = "storedAccountSettings";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String androidId;
    private boolean firstAppStart;

    private DatabaseHandler dbHandler = new DatabaseHandler(this);
    private User mainUser;
    private Context context;

//---------------------------------------ON-CREATION----------------------------------------------\\
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set layout and create scene
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Instantiate the preferences and a editor for the preferences
        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        editor = preferences.edit();

        //Check in the stored preferences if it is the first time the application starts on this phone
        firstAppStart = preferences.getBoolean("firstAppStart", true);
        Intent intent = getIntent();
        context = getApplicationContext();

        Log.d("BEFORE", " LOOP");
        if(DataStorage.getInstance().isFromReview() == true){
            Log.d("DEVICE ID: ", intent.getExtras().getString("androidID"));
            androidId = intent.getExtras().getString("androidID");

        }else{

            androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("DEVICE ID: ", androidId);
        }

        if(firstAppStart == true && DataStorage.getInstance().isFromReview() == false){
            //If it is the first time do the following:

            //Create a pop-up dialog that prompts the user to enter a username
            createAlertDialog();

        }
        else{
            DataStorage.getInstance().setFromReview(false);
            Log.d("Doing other shiz", "pew");
          //If it is not the first time, do the following:
            //getUserData(androidId);

            //Load recent reviews by user from the database
            //setRecentReviews(androidId);

            //For testing
            editor.clear();
            editor.commit();
            //---------------

        }

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

                        //Load recent reviews by user from the database
                        setRecentReviews(androidId);
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

//---------------------------------------UPDATING METHODS-----------------------------------------\\
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

    public void setRecentReviews(String androidId){
        dbHandler.getAllReviewsFromUser(androidId, new DatabaseHandler.callbackGetAllReviewsFromUser() {
            @Override
            public void onSuccess(ArrayList<ReviewObject> reviewList) {
                recentReviewsList = reviewList;
                final ListView recentReviews = (ListView) findViewById(R.id.recentReviews);
                adapter = new ReviewAdapter(context, recentReviewsList);

                recentReviews.setAdapter(adapter);
            }
        });




    }

    private void getUserData(String deviceId){
        dbHandler.getUserData(deviceId, new DatabaseHandler.callbackGetUserData() {
            @Override
            public void onSuccess(User user) {
                Log.d("User: ", "   Device_id: " + user.getDevice_id() + "   Name: " + user.getName() + "   Likes: " + user.getLikes() + "   Dislikes: " + user.getDislikes());
                mainUser = new User(user.getDevice_id(), user.getName(), user.getLikes(), user.getDislikes());

                updateUserName(mainUser.getName());
                updateLikes(String.valueOf(mainUser.getLikes()));
                updateDislikes(String.valueOf(mainUser.getDislikes()));

                //Calculate the user's level and update it
                int level = (mainUser.getLikes() - mainUser.getDislikes()) / 1;
                updateLevel(String.valueOf(level));
            }
        });

    }

    private void createUser(final String deviceId, final String userName){
        dbHandler.addUser(deviceId, userName, new DatabaseHandler.callbackAddUserComplete() {
            @Override
            public void onSuccess(String complete) {
                Log.d("User was created: ", "Device id: "+ deviceId + " Username: " + userName + complete);
                getUserData(deviceId);
            }
        });

    }

}
