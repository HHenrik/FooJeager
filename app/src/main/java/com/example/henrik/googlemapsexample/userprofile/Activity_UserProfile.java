package com.example.henrik.googlemapsexample.userprofile;

//----------------------------------------IMPORTS-------------------------------------------------\\

//Android imports
import android.app.AlertDialog;
import android.content.DialogInterface;
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


        if(firstAppStart == true){
            //If it is the first time do the following:

            //Get the android id (phone id) and save it to a string
            androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            //Create a pop-up dialog that prompts the user to enter a username
            createAlertDialog();
        }
        else{
            //If it is not the first time, do the following:

            //Load username from preferences
            updateUserName(preferences.getString("userName", "-"));

            //Load the users likes and dislikes from the database
            updateLikes("80");
            updateDislikes("40");

            //Calculate the user's level and update it
            int level = (Integer.parseInt("80") - Integer.parseInt("40")) / 10;
            updateLevel(String.valueOf(level));

            //Load recent reviews by user from the database
            setRecentReviews();
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
                        //Store the name that is entered to stored preferences
                        editor.putString("userName", userInput.getText().toString());

                        //Store the android id (phone id) to the stored preferences
                        editor.putString("androidID", androidId);

                        //Set it to that the application has been started once on this device and save changes to the preferences
                        editor.putBoolean("firstAppStart", false);
                        editor.commit();

                        //Load username from preferences
                        updateUserName(preferences.getString("userName", "-"));

                        //Load the users likes and dislikes from the database
                        updateLikes("80");
                        updateDislikes("40");

                        //Calculate the user's level and update it
                        int level = (Integer.parseInt("80") - Integer.parseInt("40")) / 10;
                        updateLevel(String.valueOf(level));

                        //Load recent reviews by user from the database
                        setRecentReviews();
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



        dbHandler.getUserData("2122", new DatabaseHandler.callbackGetUserData() {
            @Override
            public void onSuccess(User user) {

                Log.d("User: ", "   Device_id: " + user.getDevice_id() + "   Name: " + user.getName() + "   Likes: " + user.getLikes() + "   Dislikes: " + user.getDislikes());
            }
        });

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
    /*    recentReviewsList.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Hans Villius", "Mannen i restaurangen vet inte att han snart kommer bli sparkad", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 2, 0, 0, 0, 0, 0, 0, "Kent-Jonas", "Möglig mat men den var ändå ätbar. Otrevlig personal men jag återkommer ändå.", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "Lina Åkesson", "Top notch, fan rätt schysst asså! Äter mer än gärna här igen.", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 4, 0, 0, 0, 0, 0, 0, "G-son", "", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Gördis 47år", "Inte mycket att hänga i granen men helt ok", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 5, 0, 0, 0, 0, 0, 0, "Magic Mike", "https://www.youtube.com/channel/UCPlV0OpQMImKviSTWHJEDi", "", "", ""));
        recentReviewsList.add(new ReviewObject(0, 3, 0, 0, 0, 0, 0, 0, "Nicklas Brisendal", "Äter hellre på khai mui.", "", "", ""));
*/
        final ListView recentReviews = (ListView) findViewById(R.id.recentReviews);
        adapter = new ReviewAdapter(this, recentReviewsList);

        recentReviews.setAdapter(adapter);
    }

}
