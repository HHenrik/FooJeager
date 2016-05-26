package com.example.henrik.googlemapsexample.filtermenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.example.henrik.googlemapsexample.R;

public class Activity_FilterMenu extends AppCompatActivity implements View.OnClickListener {
    private String SAVED_INFO = "savedFilters";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ToggleButton fastFoodToggle;
    private ToggleButton fineDiningToggle;
    private ToggleButton pubToggle;
    private ToggleButton cafeToggle;
    private ToggleButton romanticToggle;
    private ToggleButton buffetToggle;

    private ToggleButton asianToggle;
    private ToggleButton burgerToggle;
    private ToggleButton pizzaToggle;
    private ToggleButton indianToggle;
    private ToggleButton sushiToggle;
    private ToggleButton meatToggle;
    private ToggleButton seafoodToggle;
    private ToggleButton italianToggle;
    private ToggleButton vegetarianToggle;

    private boolean firstBoot = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_menu);

        //Open up shared preferences to be able to edit
        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        editor = preferences.edit();



        //Initialize the buttons to corresponding ID
        fastFoodToggle = (ToggleButton) findViewById(R.id.fastFoodToggle);
        fineDiningToggle = (ToggleButton) findViewById(R.id.fineDiningToggle);
        pubToggle = (ToggleButton) findViewById(R.id.pubToggle);
        cafeToggle = (ToggleButton) findViewById(R.id.cafeToggle);
        romanticToggle = (ToggleButton) findViewById(R.id.romanticToggle);
        buffetToggle = (ToggleButton) findViewById(R.id.buffetToggle);

        asianToggle = (ToggleButton) findViewById(R.id.asianToggle);
        burgerToggle = (ToggleButton) findViewById(R.id.burgerToggle);
        pizzaToggle = (ToggleButton) findViewById(R.id.pizzaToggle);
        indianToggle = (ToggleButton) findViewById(R.id.indianToggle);
        sushiToggle = (ToggleButton) findViewById(R.id.sushiToggle);
        meatToggle = (ToggleButton) findViewById(R.id.meatToggle);
        seafoodToggle = (ToggleButton) findViewById(R.id.seafoodToggle);
        italianToggle = (ToggleButton) findViewById(R.id.italianToggle);
        vegetarianToggle = (ToggleButton) findViewById(R.id.vegetarianToggle);
        SeekBar priceAdjuster = (SeekBar) findViewById(R.id.priceAdjuster);

        priceAdjuster.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(firstBoot == false) {
                    editor.putInt("priceSet", progress);
                    Log.d("Set price:", String.valueOf(progress));
                    editor.commit();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        //Do the onClick method if any of the buttons are clicked
        fastFoodToggle.setOnClickListener(this);
        fineDiningToggle.setOnClickListener(this);
        pubToggle.setOnClickListener(this);
        cafeToggle.setOnClickListener(this);
        romanticToggle.setOnClickListener(this);
        buffetToggle.setOnClickListener(this);

        asianToggle.setOnClickListener(this);
        burgerToggle.setOnClickListener(this);
        pizzaToggle.setOnClickListener(this);
        indianToggle.setOnClickListener(this);
        sushiToggle.setOnClickListener(this);
        meatToggle.setOnClickListener(this);
        seafoodToggle.setOnClickListener(this);
        italianToggle.setOnClickListener(this);
        vegetarianToggle.setOnClickListener(this);

        //Load default preferences if none are already set
        fastFoodToggle.setChecked(preferences.getBoolean("Fast%20Food", false));
        fineDiningToggle.setChecked(preferences.getBoolean("Fine%20Dining", false));
        pubToggle.setChecked(preferences.getBoolean("Pub", false));
        cafeToggle.setChecked(preferences.getBoolean("Cafe", false));
        romanticToggle.setChecked(preferences.getBoolean("Romantic", false));
        buffetToggle.setChecked(preferences.getBoolean("Buffet", false));

        asianToggle.setChecked(preferences.getBoolean("Asian", false));
        burgerToggle.setChecked(preferences.getBoolean("Burger", false));
        pizzaToggle.setChecked(preferences.getBoolean("Pizza", false));
        indianToggle.setChecked(preferences.getBoolean("Indian", false));
        sushiToggle.setChecked(preferences.getBoolean("Sushi", false));
        meatToggle.setChecked(preferences.getBoolean("Meat", false));
        seafoodToggle.setChecked(preferences.getBoolean("Sea%20Food", false));
        italianToggle.setChecked(preferences.getBoolean("Italian", false));
        vegetarianToggle.setChecked(preferences.getBoolean("Vegetarian", false));

        Log.d("Value is: ", String.valueOf(preferences.getInt("priceSet",2)));
        priceAdjuster.setProgress(preferences.getInt("priceSet", 2));
        firstBoot = false;
    }

    @Override
    public void onClick(View view) {
        Log.d("OnClick:", "Changing preferences");

        switch (view.getId()) {
            case R.id.fastFoodToggle:
                if(fastFoodToggle.isChecked() == true){
                    editor.putBoolean("Fast%20Food", true);
                    Log.d("FastFood", "Checked");
                }
                else{
                    editor.putBoolean("Fast%20Food", false);
                    Log.d("FastFood", "Not Checked");
                }
                break;
            case R.id.fineDiningToggle:
                if(fineDiningToggle.isChecked() == true){
                    editor.putBoolean("Fine%20Dining", true);
                    Log.d("FineDining", "Checked");
                }
                else{
                    editor.putBoolean("Fine%20Dining", false);
                    Log.d("FineDining", "Not Checked");
                }
                break;
            case R.id.pubToggle:
                if(pubToggle.isChecked() == true){
                    editor.putBoolean("Pub", true);
                    Log.d("Pub", "Checked");
                }
                else{
                    editor.putBoolean("Pub", false);
                    Log.d("Pub", "Not Checked");
                }
                break;
            case R.id.cafeToggle:
                if(cafeToggle.isChecked() == true){
                    editor.putBoolean("Cafe", true);
                    Log.d("Cafe", "Checked");
                }
                else{
                    editor.putBoolean("Cafe", false);
                    Log.d("Cafe", "Not Checked");
                }
                break;
            case R.id.romanticToggle:
                if(romanticToggle.isChecked() == true){
                    editor.putBoolean("Romantic", true);
                    Log.d("Romantic", "Checked");
                }
                else{
                    editor.putBoolean("Romantic", false);
                    Log.d("romantic", "Not Checked");
                }
                break;
            case R.id.buffetToggle:
                if(buffetToggle.isChecked() == true){
                    editor.putBoolean("Buffet", true);
                    Log.d("Buffet", "Checked");
                }
                else{
                    editor.putBoolean("Buffet", false);
                    Log.d("Buffet", "Not Checked");
                }
                break;
            case R.id.asianToggle:
                if(asianToggle.isChecked() == true){
                    editor.putBoolean("Asian", true);
                    Log.d("Asian", "Checked");
                }
                else{
                    editor.putBoolean("Asian", false);
                    Log.d("Asian", "Not Checked");
                }
                break;
            case R.id.burgerToggle:
                if(burgerToggle.isChecked() == true){
                    editor.putBoolean("Burger", true);
                    Log.d("Burger", "Checked");
                }
                else{
                    editor.putBoolean("Burger", false);
                    Log.d("Burger", "Not Checked");
                }
                break;
            case R.id.pizzaToggle:
                if(pizzaToggle.isChecked() == true){
                    editor.putBoolean("Pizza", true);
                    Log.d("Pizza", "Checked");
                }
                else{
                    editor.putBoolean("Pizza", false);
                    Log.d("Pizza", "Not Checked");
                }
                break;
            case R.id.indianToggle:
                if(indianToggle.isChecked() == true){
                    editor.putBoolean("Indian", true);
                    Log.d("Indian", "Checked");
                }
                else{
                    editor.putBoolean("Indian", false);
                    Log.d("Indian", "Not Checked");
                }
                break;
            case R.id.sushiToggle:
                if(sushiToggle.isChecked() == true){
                    editor.putBoolean("Sushi", true);
                    Log.d("Sushi", "Checked");
                }
                else{
                    editor.putBoolean("Sushi", false);
                    Log.d("Sushi", "Not Checked");
                }
                break;
            case R.id.meatToggle:
                if(meatToggle.isChecked() == true){
                    editor.putBoolean("Meat", true);
                    Log.d("Meat", "Checked");
                }
                else{
                    editor.putBoolean("Meat", false);
                    Log.d("Meat", "Not Checked");
                }
                break;
            case R.id.seafoodToggle:
                if(seafoodToggle.isChecked() == true){
                    editor.putBoolean("Sea%20Food", true);
                    Log.d("SeaFood", "Checked");
                }
                else{
                    editor.putBoolean("Sea%20Food", false);
                    Log.d("SeaFood", "Not Checked");
                }
                break;
            case R.id.italianToggle:
                if(italianToggle.isChecked() == true){
                    editor.putBoolean("Italian", true);
                    Log.d("Italian", "Checked");
                }
                else{
                    editor.putBoolean("Italian", false);
                    Log.d("Italian", "Not Checked");
                }
                break;
            case R.id.vegetarianToggle:
                if(vegetarianToggle.isChecked() == true){
                    editor.putBoolean("Vegetarian", true);
                    Log.d("Vegetarian", "Checked");
                }
                else{
                    editor.putBoolean("Vegetarian", false);
                    Log.d("Vegetarian", "Not Checked");
                }
                break;
        }

        editor.commit();
    }

}
