package com.example.henrik.googlemapsexample.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.henrik.googlemapsexample.favourites.Activity_Favourites;
import com.example.henrik.googlemapsexample.filtermenu.Activity_FilterMenu;
import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.userprofile.Activity_UserProfile;

/**
 * Created by Henrik on 2016-05-03.
 */
public class MainMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }

    public void buttonClicked(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.favouritesButton:
                intent = new Intent(MainMenu.this, Activity_Favourites.class);
                startActivity(intent);
                break;
            case R.id.storedMenuButton:
                //intent = new Intent(MainMenu.this, Activity_StoredMenuView.class);
                //startActivity(intent);
                break;
            case R.id.profileButton:
                intent = new Intent(MainMenu.this, Activity_UserProfile.class);
                startActivity(intent);
                break;
            case R.id.filterButton:
                intent = new Intent(MainMenu.this, Activity_FilterMenu.class);
                startActivity(intent);
                break;
        }

    }

}
