package com.example.henrik.googlemapsexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    public void button(View view){
        //Intent intent = new Intent(MainMenu.this, FrameDataMenu.class);   //Lägg till rätt namn för frameData Klassen
       // startActivity(intent);
    }

}
