package com.example.henrik.googlemapsexample;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity implements LocationListener {
    private GoogleMap map;
    private boolean mapTypeChanger = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUserLocationOnMap();
        //Intent intent = new Intent(getApplicationContext(), AccelometerListener.class);
        //startActivity(intent);


    }
    private void setUserLocationOnMap() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //   map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Här är du"));
                //LatLng loc = new LatLng (location.getLatitude(), location.getLongitude());   Denna lösning zoomar inte in och tvingar inte heller inzoomning
                //map.animateCamera(CameraUpdateFactory.newCameraPosition(map.getCameraPosition()));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).bearing(90).tilt(35).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        };
        map.setOnMyLocationChangeListener(myLocationChangeListener);

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void setMapType(View view) {
        if (mapTypeChanger) {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mapTypeChanger = false;
        } else {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mapTypeChanger = true;
        }
    }
    public void mainMenuButton(View view){
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
    }
    /*public void filterMenuButton(View view){
        Intent intent = new Intent(MainActivity.this, FilterMenu.class);
        startActivity(intent);
    }*/


}
