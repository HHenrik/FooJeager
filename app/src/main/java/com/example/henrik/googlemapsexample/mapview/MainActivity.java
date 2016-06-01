package com.example.henrik.googlemapsexample.mapview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.filtermenu.Activity_FilterMenu;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.globalclasses.DatabaseHandler;
import com.example.henrik.googlemapsexample.mainmenu.MainMenu;
import com.example.henrik.googlemapsexample.restaurant.Restaurant;
import com.example.henrik.googlemapsexample.restaurant.RestaurantActivity;
import com.example.henrik.googlemapsexample.restaurant.RestaurantAdapter;
import com.example.henrik.googlemapsexample.review.ReviewObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MainActivity extends FragmentActivity implements LocationListener , SensorEventListener {
    private GoogleMap map;
    private boolean mapTypeChanger = true;
    private Marker[] resturantMarkers;
    private ArrayList<Restaurant> restaurantList = new ArrayList();
    private MarkerOptions[] resturantsMarkerOptions;
    private String nextPageToken;
    private boolean oncePage1 = true;
    private boolean testbBoolean = true;
    private boolean restaurantDetail = true;
    private String restaurantDetailHTML;
    private int savedObjectId;
    private SlidingUpPanelLayout restaurantViewSlidePanel;
    private Restaurant restaurantTemp;
    private boolean databaseIsOnline = false;
    //private ArrayList<String> restaurantNames = new ArrayList();
    //private ArrayList<Float> restaurantGoogleRatings = new ArrayList();
    //private ArrayList<Float> restaurantLocations = new ArrayList();

    private boolean markerHiding = false;
    private boolean loadingisDone = false;
    private boolean restaurantDetailActivated = false;
    private Sensor sensor;
    private SensorManager sensorManager;

    private RestaurantAdapter restaurantAdapter;

    private float currentAccelerationValue = SensorManager.GRAVITY_EARTH;
    private float previousAccelerationValue = SensorManager.GRAVITY_EARTH;
    private float accelerationValue = 0.00f;
    private boolean firstTimeStart = true;
    private int savedArraySize = 0;

    private ListView restaurantViewListView;
    private int previusClickedID;

    private Button sortButton;
    private ArrayAdapter<String> arrayAdapter;

    private int sortChooser = 0;
    private int numberOfHTMLpages = 0;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String SAVED_INFO = "savedFilters";
    private boolean firstResume = false;
    private ArrayList <String> restaurantFilters = new ArrayList();
    private ArrayList <String> restaurantFiltersSorted = new ArrayList();

    private ArrayList <String> restaurantFilteredID  = new ArrayList();
    private ArrayList <Restaurant> filteredRestaurantList  = new ArrayList();
    private boolean skit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resturantMarkers = new Marker[60];//Max antal restauranger som visas
        setUserLocationOnMap();
        if(!DataStorage.getInstance().isUserPositionSupport()){
            LatLng kristianstadCords = new LatLng(56.0333333, 14.1333333);
            zoomMapToMarker(kristianstadCords, 12);
        }
        sortButton = (Button) findViewById(R.id.sortButton);
        sortButton.setVisibility(View.GONE);

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(restaurantList);
        prefsEditor.putString("ResturantObjectList", json);
        prefsEditor.commit();
        setUpAccelerometer();
        restaurantViewSlidePanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        restaurantViewListView = (ListView) findViewById(R.id.restaurantList);

      /*  GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .build();

*/
     //   setUpRestaurantFilterArray();
        setUpRestaurantFilterArray();
        getfilteredID(restaurantFiltersSorted);

       /* Log.d("before","filterResult");
        DatabaseHandler handler = new DatabaseHandler(this);
        Log.d("DatabaseHandler","filterResult");
        ArrayList<String> sven = new ArrayList();
        sven.add("Burger");
        sven.add("Romantic");


        handler.getRestaurantWithFilter(sven, new DatabaseHandler.callbackGetRestaurantWithFilter() {

            @Override
            public void onSuccess(ArrayList<String> restaurantList) {
                Log.d("onSuccsess","filterResult");
                Log.d(restaurantList.get(0).toString(),"filterResult");
              for(int i=0;i<restaurantList.size();i++)
                Log.d(restaurantList.get(i).toString(),"filterResult");
            }
        });*/
        firstTimeStart = false;
    }

        // put your code here...
    private void filterRestaurantList(){
        for(int i=0;i<restaurantFilteredID.size();i++){
            for(int h=0;h<restaurantList.size();h++){
                if(restaurantList.get(h).getId().equals(restaurantFilteredID.get(i))){
                    filteredRestaurantList.add(restaurantList.get(h));
                }
            }
        }
    }

    private void getfilteredID(ArrayList filterOptions){
      //  Log.d(Integer.toString(filterOptions.size()),"IdOfFilters");
        if(restaurantFiltersSorted.size()!=0&&restaurantFiltersSorted.size()!=15){
            DatabaseHandler handler = new DatabaseHandler(this);
            handler.getRestaurantWithFilter(filterOptions, new DatabaseHandler.callbackGetRestaurantWithFilter() {
                @Override
                public void onSuccess(ArrayList<String> restaurantList) {
                    databaseIsOnline = true;
                    for(int i = 0;i<restaurantList.size();i++){
                        Log.d("KorrektaId",restaurantList.get(i));
                        if(!restaurantFilteredID.contains(restaurantList.get(i))) //contains ska jag sätta på andra ställen också istället för onödiga loopar!!!!
                             restaurantFilteredID.add(restaurantList.get(i));

                    }
                    if (skit) {
                        filterRestaurantList();
                        hideFilteredMarkersFromMap();
                        sortRestarantList("Sorted on Names",4);

                    }


                }


            });
            if (databaseIsOnline == false){
                //Gör så att alla syns
            }
        }
      else if(firstResume==true&&restaurantFiltersSorted.size()==0){
           for(int i = 0;i<restaurantList.size();i++){
               restaurantList.get(i).getMarker().setVisible(false);
           }
        }
        else if(firstResume==true&&restaurantFiltersSorted.size()==15){
            filteredRestaurantList = restaurantList;
            hideFilteredMarkersFromMap();
            sortRestarantList("Sorted on Names",4);
        }


    }
    private void setUpRestaurantFilterArray(){
        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        restaurantFilters.add("Fast%20Food");
        restaurantFilters.add("Fine%20Dining");
        restaurantFilters.add("Pub");
        restaurantFilters.add("Cafe");
        restaurantFilters.add("Romantic");
        restaurantFilters.add("Buffet");
        restaurantFilters.add("Asian");
        restaurantFilters.add("Burger");
        restaurantFilters.add("Pizza");
        restaurantFilters.add("Indian");
        restaurantFilters.add("Sushi");
        restaurantFilters.add("Meat");
        restaurantFilters.add("Sea%20Food");
        restaurantFilters.add("Italian");
        restaurantFilters.add("Vegetarian");

       for(int i = 0;i<15;i++) {
           if (preferences.getBoolean(restaurantFilters.get(i), true)) {
               restaurantFiltersSorted.add(restaurantFilters.get(i));
           }
       } Log.d(Integer.toString(restaurantFiltersSorted.size()),"fsgdghd");
/*
        if(preferences.getBoolean(restaurantFilters.get(0),true)){
                restaurantFiltersSorted.add(restaurantFilters.get(0));
        }
        if(preferences.getBoolean(restaurantFilters.get(1),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(1));
        }
        if(preferences.getBoolean(restaurantFilters.get(2),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(2));
        }
        if(preferences.getBoolean(restaurantFilters.get(3),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(3));
        }
        if(preferences.getBoolean(restaurantFilters.get(4),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(4));
        }
        if(preferences.getBoolean(restaurantFilters.get(5),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(5));
        }
        if(preferences.getBoolean(restaurantFilters.get(6),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(6));
        }
        if(preferences.getBoolean(restaurantFilters.get(7),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(7));
        }
        if(preferences.getBoolean(restaurantFilters.get(8),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(8));
        }

        if(preferences.getBoolean(restaurantFilters.get(9),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(9));
        }

        if(preferences.getBoolean(restaurantFilters.get(10),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(10));
        }
        if(preferences.getBoolean(restaurantFilters.get(11),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(11));
        }
        if(preferences.getBoolean(restaurantFilters.get(12),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(12));
        }
        if(preferences.getBoolean(restaurantFilters.get(13),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(13));
        }
        if(preferences.getBoolean(restaurantFilters.get(14),true)){
            restaurantFiltersSorted.add(restaurantFilters.get(14));
        }

        Log.d(Integer.toString(restaurantFiltersSorted.size()),"hasfhuuiusduif");
*/

    }

    private void setUpAccelerometer() {
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
            findRandomRestaurantOnAccelerometer();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() { //Tydligen tar listeners energi o cpukraft. Därför onResume samt onPause.
        super.onResume();

        if(firstTimeStart && DataStorage.getInstance().isFilterIsActive()){
            firstResume = true;
            skit = true;
            restaurantFilters.clear();
            restaurantFiltersSorted.clear();
            restaurantFilteredID.clear();
            filteredRestaurantList.clear();

            restaurantList = DataStorage.getInstance().getRestaurantList();

            for(int i=0;i<restaurantList.size();i++){
                Log.d("svenssonsson","sven");
                restaurantList.get(i).getMarker().setVisible(true);
            }
            Log.d("Resume","onResume");
            setUpRestaurantFilterArray();
            getfilteredID(restaurantFiltersSorted);

           // setRestaurantListView();

            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        firstTimeStart = true;
        DataStorage.getInstance().setFilterIsActive(false);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void findRandomRestaurantOnAccelerometer() {
       /* for(int i=0;i<restaurantList.size()
        ;i++){
            restaurantList.get(i).getMarker().setVisible(true);
        }*/
        if(markerHiding){
            int maxValue = filteredRestaurantList.size()-1; //restaurantList
            int minValue = 0;
            int difference = maxValue - minValue;
            Random random = new Random();
            int result = random.nextInt(difference + 1);
            result += minValue;
            Log.d(Integer.toString(result), "random number");
            zoomMapToMarker(filteredRestaurantList.get(result).getPosition(), 18);//restaurantList
        }
    }

    private void zoomMapToMarker(LatLng markerLocation, int zoomValue) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, zoomValue));
        map.animateCamera(CameraUpdateFactory.zoomIn());
        map.animateCamera(CameraUpdateFactory.zoomTo(zoomValue), 3000, null);
    }


    private void setRestaurantListView() {



        restaurantViewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                previusClickedID = filteredRestaurantList.size() + 1;
                for (int i = 0; i < filteredRestaurantList.size(); i++) {
                   if (filteredRestaurantList.get(i).getName().equals(filteredRestaurantList.get(position).getName())) {
                       filteredRestaurantList.get(i).getMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        if (previusClickedID != filteredRestaurantList.size() + 1) {
                            filteredRestaurantList.get(previusClickedID).getMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        }
                        zoomMapToMarker(filteredRestaurantList.get(i).getPosition(),18);
                        previusClickedID = i;
                    }


                }
            }
        });
        getRestaurantDistances();

      //  Collections.sort(restaurantLocations);
        //Collections.sort(restaurantGoogleRatings);
        //Collections.reverse(restaurantGoogleRatings);
        //Collections.sort(restaurantNames);
        //for(int i=0;i<restaurantGoogleRatings.size();i++){
          //  Log.d(restaurantGoogleRatings.get(i).toString(),"Collections");
        //}

        filterRestaurantList();
        hideFilteredMarkersFromMap();
        restaurantAdapter = new RestaurantAdapter(this, filteredRestaurantList);



        restaurantViewSlidePanel.setScrollableView(restaurantViewListView);
      //  arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurantNames);
      /*  arrayAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String leftValue, String rightValue) {
                return leftValue.compareTo(rightValue);
            }
        });*/
        restaurantViewListView.setAdapter(restaurantAdapter);

    }
    private void hideFilteredMarkersFromMap(){
        Log.d(Integer.toString(restaurantFiltersSorted.size()),"dsdfds");
        Log.d(Integer.toString(filteredRestaurantList.size()),"SeHajajaa");
        if(restaurantFiltersSorted.size()!=15) {


            boolean removeMarker;
            for (int i = 0; i < restaurantList.size(); i++) {           //Bytt till contain istället för dubbelloop
                removeMarker = true;
                for (int h = 0; h < filteredRestaurantList.size(); h++) {
                    if (restaurantList.get(i).getName().equals(filteredRestaurantList.get(h).getName())) {
                        removeMarker = false;
                        restaurantList.get(i).getMarker().setVisible(true);
                        Log.d("Titta", "matchar");
                    }
                }
                if (removeMarker) {
                    restaurantList.get(i).getMarker().setVisible(false);
                    Log.d("Titta", "sdfsdfds");
                }
            }
            //Ny förbättrad inte testad
           /* for(int i=0;i<restaurantList.size();i++){
                if(!filteredRestaurantList.contains(restaurantList.get(i))){
                    restaurantList.get(i).getMarker().setVisible(false);
                }
                else{
                    restaurantList.get(i).getMarker().setVisible(true);
                }
            }*/
        }
        else{
            filteredRestaurantList = restaurantList;
            for(int i=0;i<restaurantList.size();i++){
                restaurantList.get(i).getMarker().setVisible(true);
            }
        }
        markerHiding = true;
    }
    public void onSortButtonClicked(View view){
        sortChooser++;
            if(sortChooser==1){
                sortRestarantList("Sorted on review", sortChooser);
                Collections.reverse(filteredRestaurantList);
            }
            else if (sortChooser==2)
                sortRestarantList("Sorted on distance", sortChooser);
            else{
                sortRestarantList("Sorted on name", sortChooser);
                sortChooser=0;
            }

    }

    private void sortRestarantList(String buttonMessage, final int sortType){
        Collections.sort(filteredRestaurantList, new Comparator<Restaurant>() {
            public int compare(Restaurant leftRestaurant, Restaurant rightRestaurant) {
                if(sortType==1)
                    return leftRestaurant.getGoogleRating().compareTo(rightRestaurant.getGoogleRating());
                else if(sortType==2)
                    return leftRestaurant.getDistanceToRestaurant().compareTo(rightRestaurant.getDistanceToRestaurant());
                else
                    return leftRestaurant.getName().compareTo(rightRestaurant.getName());
            }
        });
        if(sortType!=5)
        clearRestaurantListViewAndRefreshData(buttonMessage);
    }
    private void clearRestaurantListViewAndRefreshData(String textForButton){

        ArrayList<Restaurant> restaurantTempArray = new ArrayList();

      for(int i = 0;i<filteredRestaurantList.size();i++){
          restaurantTempArray.add(filteredRestaurantList.get(i));
      }

        restaurantAdapter.clear();
        restaurantAdapter.addAll(restaurantTempArray);
        restaurantAdapter.notifyDataSetChanged();
        sortButton.setText(textForButton);
        Log.d("done","sgdfggg");
    }



    private void getRestaurantDistances(){
        Location userLocation = new Location("userLocation");
        if(DataStorage.getInstance().isUserPositionSupport()){
            userLocation.setLatitude(DataStorage.getInstance().getUserPosition().latitude);
            userLocation.setLongitude(DataStorage.getInstance().getUserPosition().longitude);
        }
        else{
            userLocation.setLatitude(56.0333333);
            userLocation.setLongitude(14.1333333);
        }
        Location restaurantLocation = new Location("restaurantLocation");

        for(int i=0;i<restaurantList.size();i++){
            restaurantLocation.setLatitude(restaurantList.get(i).getPosition().latitude);
            restaurantLocation.setLongitude(restaurantList.get(i).getPosition().longitude);
            float distance = userLocation.distanceTo(restaurantLocation);
            restaurantList.get(i).setDistanceToRestaurant(distance);
            //restaurantLocations.add(distance);
        }
        /*
        for(int i=0;i<restaurantList.size();i++){
            Log.d(restaurantList.get(i).getDistanceToRestaurant().toString(),"Getyoureyeshere");
        }
        */
    }

    public void panelListener() {

        restaurantViewSlidePanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            // During the transition of expand and collapse onPanelSlide function will be called.
            @Override
            public void onPanelSlide(View panel, float slideOffset) {


            }

            // Called when secondary layout is dragged up by user
            @Override
            public void onPanelExpanded(View panel) {
                sortButton.setActivated(true);
                sortButton.setVisibility(View.VISIBLE);
            }

            // Called when secondary layout is dragged down by user
            @Override
            public void onPanelCollapsed(View panel) {
               sortButton.setVisibility(View.GONE);

            }

            @Override
            public void onPanelAnchored(View panel) {


            }

            @Override
            public void onPanelHidden(View panel) {


            }
        });
    }

    private void removeIndividualMarker(String restaurantName) {
        for (int i = 0; i < restaurantList.size(); i++) {
            if (restaurantList.get(i).getName().equals(restaurantName)) {
                restaurantList.get(i).getMarker().setVisible(false);
            }


        }
    }

    private void setUserLocationOnMap() {
        final double tesLat = 56.03129;
        final double testLong = 14.15242;
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                DataStorage.getInstance().setUserPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                //   map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Här är du"));
                //LatLng loc = new LatLng (location.getLatitude(), location.getLongitude());   Denna lösning zoomar inte in och tvingar inte heller inzoomning
                //map.animateCamera(CameraUpdateFactory.newCameraPosition(map.getCameraPosition()));
                //location.getLatitude()+           location.getLongitude()
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).bearing(90).tilt(35).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                DataStorage.getInstance().setUserPositionSupport(true);
            }
        };
        map.setOnMyLocationChangeListener(myLocationChangeListener);


        map.setOnInfoWindowClickListener( //När man trycker på texten så ska activty eller fragment öppnas med information om den restaurangen
                new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {
                        for (int i = 0; i < restaurantList.size(); i++) {
                            if (restaurantList.get(i).getName().equals(marker.getTitle())) {//De restaurangerna som är knäppa finns inte i arrayen. Därför blir det fel namn
                                restaurantDetailHTML = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + restaurantList.get(i).getId() + "&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE";
                                savedObjectId = i;
                                DataStorage.getInstance().setActiveRestaurant(savedObjectId);
                            }
                        }
                        restaurantDetailActivated = true;
                        new GetRestaurantData().execute(restaurantDetailHTML);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                        startActivity(intent);
                    }
                }
        );
        /*

          for(int i = 0;i<restaurantList.size();i++){
                    if(restaurantList.get(i).getMarker()!=null){
                        Log.d("Has marker","MNullchecker");
                        restaurantList.get(i).getMarker().setVisible(false);
                    }
                    else{
                        Log.d("NULL marker","MNullchecker");
                    }
         */


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()//Denna metod tar dock bort så att namnet inte längre syns när man klickar. Ha namnet alltid där? Eller bättre att bara erbjuda att öppna och inte tvinga användaren!
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0 != null) ;
                arg0.showInfoWindow();
                Log.d(arg0.getTitle(), "PlaceName");
                return true;
            }

        });

        String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/search/json?location=%2056.03129,14.15242&radius=10000&sensor=true&&types=restaurant&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE";
        //Kordinater för Kristianstad. Kan byta till location.getLatitude(), location.getLongitude()
        if (testbBoolean) { //testBoolean tillfällig. Annars kommer den att anropas varje gång som användaren byter postion.
            new GetRestaurantData().execute(googlePlacesUrl);
        }
        testbBoolean = false;
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

    public void mainMenuButton(View view) {
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void filterMenuButton(View view) {
        //Tillfälligt här. Använda removeindivdualmarker() istället vid filtering.
        //restaurantList.get(0).getMarker().setVisible(false); //Antingen gömma undan eller ta bort helt. Om ta bort måste vi spara undan MarkerOptions[] i resturangobjektet också pga att den behövs för att skapa markeringen
        Intent intent = new Intent(MainActivity.this, Activity_FilterMenu.class);
        startActivity(intent);
    }


    public class GetRestaurantData extends AsyncTask<String, Void, String> { //Flytta ut till ett eget klassdokument

        @Override
        protected String doInBackground(String... googlePlacesURL) {
            StringBuilder returnedTextBuilder = new StringBuilder();

            for (String googlePlacesSearchURL : googlePlacesURL) {
                HttpClient urlExecuteClient = new DefaultHttpClient();
                try {
                    HttpGet httpGetter = new HttpGet(googlePlacesSearchURL);
                    HttpResponse httpResponse = urlExecuteClient.execute(httpGetter);

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity httpEntity = httpResponse.getEntity();
                        InputStream stream = httpEntity.getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(stream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String linRead;
                        while ((linRead = bufferedReader.readLine()) != null) {
                            returnedTextBuilder.append(linRead);
                        }

                        Log.d(returnedTextBuilder.toString(), "PlaceName");

                    } else {
                        Log.d("Wrong status code. Fail", "Status Code");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d(returnedTextBuilder.toString(), "Placename");
            return returnedTextBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("On Post execute", "PlaceName");
            //  removeAllCurrentRestaurantMarkersFromMarkerArray();
            if (restaurantDetail) {


                parseJsonData(result);
                placeAllRestaurantMarkersOnMap();
                for (int i = savedArraySize; i < restaurantList.size(); i++) {  //Flyttat hit pga måste köras efter att vi fått hem samtliga pins
               /*     if(resturantMarkers[i]==null){
                        Log.d("Null value","NullvalueonPost");
                        Log.d(restaurantList.get(i).getName(),"NullvalueonPost");
                        restaurantList.get(i).setMarker(restaurantList.get(0).getMarker());

                    }
                    else*/
                    restaurantList.get(i).setMarker(resturantMarkers[i]);

                }

                try {
                    Thread.sleep(2400);//2400 för sida 3
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String nextPageTokenURL = "https://maps.googleapis.com/maps/api/place/search/json?location=%2056.03129,14.15242&radius=10000&sensor=true&&types=restaurant&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE&pagetoken=" + nextPageToken;

                if (nextPageToken!=null) {    //nextPageToken!=null för sida 3
                    oncePage1 = false;
                    savedArraySize = restaurantList.size(); //Felet är att de nya restaurangerna inte får fungerande markeringar eftersom börjar från start. därför krash
                    new GetRestaurantData().execute(nextPageTokenURL);
                    Log.d("Se","Jajas");
                } else {
                    restaurantDetail = false;
                    Log.d(Integer.toString(restaurantList.size()),"Arraylistsize");
                    setRestaurantListView();
                    sortRestarantList("Sorted on names",4);
                    panelListener();
                    for(int i=0;i<restaurantList.size();i++){
                        Log.d(restaurantList.get(i).getName(),"RestaurantName");
                        Log.d(restaurantList.get(i).getId(),"RestaurantName");
                    }
                    DataStorage.getInstance().setRestaurantList(restaurantList);

                }
            }






            //  sortRestaurantOnReviews();
            if (numberOfHTMLpages<3 && restaurantDetailActivated) {
                parseRestaurantDetails(result);
                Log.d("Sven","svensson");
            }

        }

        private void parseRestaurantDetails(String result) {//Fungerar

            Log.d("WOULD YOU LOOK AT THAT1", "SeHIT");
            JSONObject jsonObjectResult = null;
            try {
                jsonObjectResult = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jObjectResult = (JSONObject) jsonObjectResult.get("result");

                restaurantList.get(savedObjectId).setPhoneNumber(jObjectResult.getString("formatted_phone_number"));
                restaurantList.get(savedObjectId).setWebsiteLink(jObjectResult.getString("website"));
                ArrayList<ReviewObject> reviewArray = new ArrayList();
                JSONArray reviews = jObjectResult.getJSONArray("reviews");
                for (int i = 0; i < reviews.length(); i++) {
                    JSONObject reviewIndex = reviews.getJSONObject(i);
                    ReviewObject restaurantReviews = new ReviewObject(0, Float.parseFloat(reviewIndex.getString("rating")), 0, 0, 0, 0, 0, 0, reviewIndex.getString("text"), "", "", "");
                    restaurantReviews.setUser(reviewIndex.getString("author_name"));
                    reviewArray.add(i, restaurantReviews);

                }
                DataStorage.getInstance().getRestaurantList().get(savedObjectId).setReviews(reviewArray);

                JSONObject openHours = jObjectResult.getJSONObject("opening_hours");
                JSONArray openHoursJSONArray = openHours.getJSONArray("weekday_text");
                String[] openHourArray = new String[7];
                for (int i = 0; i < openHoursJSONArray.length(); i++) {
                    openHourArray[i] = openHoursJSONArray.getString(i);
                    Log.d(openHourArray[i], "Tittala");
                }
                restaurantList.get(savedObjectId).setOpenHoursArray(openHourArray);


            } catch (JSONException e) {

            } catch (Exception e) {

            }


        }

        private void parseJsonData(String result) {
            try {
                int markerCounter = 0;
                JSONObject jsonObjectResult = null;
                jsonObjectResult = new JSONObject(result);



                JSONArray jsonRestaurantArray = null;
                jsonRestaurantArray = jsonObjectResult.getJSONArray("results");
                resturantsMarkerOptions = new MarkerOptions[jsonRestaurantArray.length()];
                Log.d(String.valueOf(jsonRestaurantArray.length()), "PlaceName");

                for (int p = 0; p < jsonRestaurantArray.length(); p++) {
                    boolean valueIsMissing = false;//Ta bort
                    LatLng restaurantLocation = null;
                    String restaurantName = "";
                    String restaurantVicinity = "";
                    try {
                        valueIsMissing = false;
                        JSONObject jsonObjectRestaurant = jsonRestaurantArray.getJSONObject(p);
                        JSONObject locationObject = jsonObjectRestaurant.getJSONObject("geometry").getJSONObject("location");
                        restaurantName = jsonObjectRestaurant.getString("name");
                        restaurantLocation = new LatLng(Double.valueOf(locationObject.getString("lat")), Double.valueOf(locationObject.getString("lng")));
                        restaurantVicinity = jsonObjectRestaurant.getString("vicinity");


                        Restaurant restaurant = new Restaurant();


                        restaurant.setName(jsonObjectRestaurant.getString("name"));
                        //restaurantNames.add(jsonObjectRestaurant.getString("name"));
                        restaurant.setPosition(restaurantLocation);
                        restaurant.setVicinity(restaurantVicinity);
                        restaurant.setId(jsonObjectRestaurant.getString("place_id"));

                        try {
                          //  restaurantGoogleRatings.add(Float.parseFloat(jsonObjectRestaurant.getString("rating")));
                            restaurant.setGoogleRating(jsonObjectRestaurant.getString("rating"));
                        }
                        catch (JSONException exception) {
                            //restaurantGoogleRatings.add(sven);
                            restaurant.setGoogleRating("0");
                        }
                        try{
                            restaurant.setPriceLevel(jsonObjectRestaurant.getString("price_level"));
                        }
                        catch (JSONException exception) {
                            restaurant.setPriceLevel("0");
                        }
                        try{
                            JSONObject openHours = jsonObjectRestaurant.getJSONObject("opening_hours");
                            restaurant.setOpenNow(openHours.getBoolean("open_now"));
                        }
                        catch (JSONException exception) {
                            restaurant.setOpenNow(false);
                        }



                        //Log.d(jsonObjectRestaurant.getString("name"), "RestaurantName");
                        //Log.d(jsonObjectRestaurant.getString("place_id"), "RestaurantName");

                        //JSONObject openHours = jsonObjectRestaurant.getJSONObject("opening_hours");
                        //restaurant.setOpenNow(openHours.getBoolean("open_now"));
                        //Log.d(Boolean.toString(openHours.getBoolean("open_now")),"Is open now?");
                        restaurantTemp = restaurant;



                    } catch (JSONException exception) {
                        valueIsMissing = true;
                        exception.printStackTrace();
                    }
                 if(valueIsMissing){

                 }

             //           if (valueIsMissing) {//tas detta bort blir det problem med vissa markeringar blir fel och kaos.
               //         resturantsMarkerOptions[p] = null;          //Det är denna som förstör. Tas den bort så kommer några till som inte fungerar
                 //      Log.d(restaurantList.get(restaurantList.size()-1).getName(),"Dessa har trasiga markeringar"); //Hur kan de komma ut på kartan när de inte får nån markeroptions?
                   // }

                   // else {

                   else {
                     //Gamegården har två resultat. Rusab är ingen restaurang
                    // if (!restaurantTemp.getName().equals("Gamlegårdens Restaurant") && !restaurantTemp.getName().equals("Rusab")) {
                         resturantsMarkerOptions[p] = new MarkerOptions().position(restaurantLocation).title(restaurantName).icon
                                 (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet(restaurantVicinity);//Vi kan ha olika ikoner beroende på typ av restaurang eller nått sånt.
                         //}
                         markerCounter++;
                         restaurantList.add(restaurantTemp);
                     //}
                 }

                    Log.d("Placing marker", "PlaceName");



                }
                try{
                    nextPageToken = jsonObjectResult.getString("next_page_token");
                }
             catch (JSONException exception) {
                 nextPageToken = null;
                exception.printStackTrace();
            }

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }


        private void removeAllCurrentRestaurantMarkersFromMarkerArray() {//Onödig om vi bara ska söka en gång med en statisk plats
            if (resturantMarkers != null) {
                for (int placedMarker = 0; placedMarker < resturantMarkers.length; placedMarker++) {
                    if (resturantMarkers[placedMarker] != null)
                        resturantMarkers[placedMarker].remove();
                }
            }
        }

        private void placeAllRestaurantMarkersOnMap() {
            int counter = 0;
            if (resturantsMarkerOptions != null && resturantMarkers != null) {
                for (int i = 0; i < resturantsMarkerOptions.length; i++) {
                    if (resturantsMarkerOptions[i] != null) {
                        resturantMarkers[savedArraySize+counter] = map.addMarker(resturantsMarkerOptions[i]);
                        Log.d("Setting marker", "PlaceName");
                        counter++;

                    } else {
                        //lägg till counter för fungerande lista se tidigare
                        Log.d("nullvalue", "nullder");
                       counter--;
                    }

                }


            }

        }

        private void hideRestaurantOnType() {
            for (int i = 0; i < restaurantList.size(); i++) {
                //   if(){
                restaurantList.get(i).getMarker().setVisible(false);
            }
            //      else{
            // restaurantList.get(i).getMarker().setVisible(true);
        }
        //SlidingUpPanel of umano
    }
}








