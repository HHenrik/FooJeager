package com.example.henrik.googlemapsexample.globalclasses;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.henrik.googlemapsexample.R;

import java.util.Random;

/**
 * Created by Henrik on 2016-04-27.
 */
public class AccelometerListener extends Activity implements SensorEventListener {

    private TextView header;
    private Sensor sensor;
    private SensorManager sensorManager;


    private float currentAccelerationValue = SensorManager.GRAVITY_EARTH;
    private float previousAccelerationValue = SensorManager.GRAVITY_EARTH;
    private float accelerationValue = 0.00f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  header = (TextView) findViewById(R.id.header);
        setUpAccelometer();
    }

    private void setUpAccelometer() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float xValue = sensorEvent.values[0];
        float yValue = sensorEvent.values[1];
        float zValue = sensorEvent.values[2];

        previousAccelerationValue = currentAccelerationValue;
        currentAccelerationValue = (float) (float) Math.sqrt(xValue * xValue + yValue * yValue + zValue * zValue);
        float accelerationValueChange = currentAccelerationValue - previousAccelerationValue;
        accelerationValue = accelerationValue * 0.9f + accelerationValueChange;
        if (accelerationValue > 15) {
            randomNumberPrintJustForTest();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onResume() { //Tydligen tar listeners energi o cpukraft. Därför onResume samt onPause.
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void randomNumberPrintJustForTest() {
        int max = 10;
        int min = 5;
        int diff = max - min;
        Random rn = new Random();
        int i = rn.nextInt(diff + 1);
        i += min;
        header.setText("The Random Number is " + i);
    }
}