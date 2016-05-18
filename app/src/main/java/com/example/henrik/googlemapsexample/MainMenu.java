package com.example.henrik.googlemapsexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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
                //intent = new Intent(MainMenu.this, Activity_FavouritesView.class);
                //startActivity(intent);
                break;
            case R.id.storedMenuButton:
                //intent = new Intent(MainMenu.this, Activity_StoredMenuView.class);
                //startActivity(intent);
                break;
            case R.id.profileButton:
                //intent = new Intent(MainMenu.this, Activity_ProfileView.class);
                //startActivity(intent);
                break;
            case R.id.filterButton:
                intent = new Intent(MainMenu.this, Activity_FilterMenu.class);
                startActivity(intent);
                break;
        }

    }

}
