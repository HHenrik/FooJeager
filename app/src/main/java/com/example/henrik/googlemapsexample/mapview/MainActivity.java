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
import android.widget.ListView;

import com.example.henrik.googlemapsexample.filtermenu.Activity_FilterMenu;
import com.example.henrik.googlemapsexample.mainmenu.MainMenu;
import com.example.henrik.googlemapsexample.R;
import com.example.henrik.googlemapsexample.globalclasses.DataStorage;
import com.example.henrik.googlemapsexample.restaurant.Restaurant;
import com.example.henrik.googlemapsexample.restaurant.RestaurantActivity;
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
    private ArrayList <String> restaurantNames = new ArrayList();


    private Sensor sensor;
    private SensorManager sensorManager;


    private float currentAccelerationValue = SensorManager.GRAVITY_EARTH;
    private float previousAccelerationValue = SensorManager.GRAVITY_EARTH;
    private float accelerationValue = 0.00f;

    private int savedArraySize = 0;

    private ListView restaurantViewListView;
    private int previusClickedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resturantMarkers = new Marker[60];//Max antal restauranger som visas
        setUserLocationOnMap();



        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(restaurantList);
        prefsEditor.putString("ResturantObjectList", json);
        prefsEditor.commit();
        setUpAccelometer();
        restaurantViewSlidePanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        restaurantViewListView = (ListView) findViewById(R.id.restaurantList);

      /*  GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .build();
*/

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
        Log.d(Integer.toString(i),"random number");
    }
    private void zoomMapToMarker(LatLng markerLocation){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation,18));
        map.animateCamera(CameraUpdateFactory.zoomIn());
        map.animateCamera(CameraUpdateFactory.zoomTo(18), 3000, null);
    }
    private void setRestaurantListView(){

        restaurantViewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i=0;i<restaurantList.size();i++){
                    if(restaurantList.get(i).getName().equals(restaurantNames.get(position))) {
                            restaurantList.get(i).getMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        if (previusClickedID != restaurantList.size()+1) {
                            restaurantList.get(previusClickedID).getMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        }
                        zoomMapToMarker(restaurantList.get(i).getPosition());
                        previusClickedID = i;
                        Log.d(restaurantList.get(i).getMarker().getTitle(),"Lookd");
                    }


                }
            }
        });
        restaurantViewSlidePanel.setScrollableView(restaurantViewListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurantNames);
        arrayAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String leftValue, String rightValue) {
                return leftValue.compareTo(rightValue);
            }
        });
        restaurantViewListView.setAdapter(arrayAdapter);
    }

    public void panelListener(){

        restaurantViewSlidePanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            // During the transition of expand and collapse onPanelSlide function will be called.
            @Override
            public void onPanelSlide(View panel, float slideOffset) {


            }

            // Called when secondary layout is dragged up by user
            @Override
            public void onPanelExpanded(View panel) {


            }

            // Called when secondary layout is dragged down by user
            @Override
            public void onPanelCollapsed(View panel) {


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
                            if (restaurantList.get(i).getName().equals(marker.getTitle())) {
                                restaurantDetailHTML = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + restaurantList.get(i).getId() + "&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE";
                                savedObjectId = i;
                                DataStorage.getInstance().setActiveRestaurant(savedObjectId);
                            }
                        }
                        new GetRestaurantData().execute(restaurantDetailHTML);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        DataStorage.getInstance().setRestaurantList(restaurantList);
                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                        startActivity(intent);
                    }
                }
        );


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
                    Thread.sleep(1200);//värdet är inte optimerat för tillfället. Kan säkert vara lägre. Men behövs annars blir det skit av det...
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String nextPageTokenURL = "https://maps.googleapis.com/maps/api/place/search/json?location=%2056.03129,14.15242&radius=10000&sensor=true&&types=restaurant&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE&pagetoken=" + nextPageToken;

                if (oncePage1) {
                    oncePage1 = false;
                    savedArraySize = restaurantList.size(); //Felet är att de nya restaurangerna inte får fungerande markeringar eftersom börjar från start. därför krash
                    new GetRestaurantData().execute(nextPageTokenURL);
                } else {
                    restaurantDetail = false;
                }
            }

            previusClickedID=restaurantList.size()+1;
            setRestaurantListView();
            panelListener();
            if (restaurantDetail == false) {
                parseRestaurantDetails(result);
            }
            DataStorage.getInstance().setPlacedLoaded(true);
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
                ReviewObject restaurantReviews = new ReviewObject(0, Float.parseFloat(reviewIndex.getString("rating")), 0, 0, 0, 0, 0, 0, reviewIndex.getString("text"),"","","");
                reviewArray.add(i, restaurantReviews);
            }
            DataStorage.getInstance().getRestaurantList().get(savedObjectId).setReviews(reviewArray);

            JSONObject openHours = jObjectResult.getJSONObject("opening_hours");
            JSONArray openHoursJSONArray = openHours.getJSONArray("weekday_text");
            String[] openHourArray = new String[7];
            for(int i=0;i<openHoursJSONArray.length();i++){
                openHourArray[i]=openHoursJSONArray.getString(i);
                Log.d( openHourArray[i],"Tittala");
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

            nextPageToken = jsonObjectResult.getString("next_page_token");

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

                    restaurant.setGoogleRating(jsonObjectRestaurant.getString("rating"));
                    restaurant.setName(jsonObjectRestaurant.getString("name"));
                    restaurantNames.add(jsonObjectRestaurant.getString("name"));
                    restaurant.setPosition(restaurantLocation);
                    restaurant.setVicinity(restaurantVicinity);
                    restaurant.setId(jsonObjectRestaurant.getString("place_id"));
                    restaurant.setPriceLevel("price_level");
                    Log.d(jsonObjectRestaurant.getString("name"),"RestaurantName");
                    Log.d(jsonObjectRestaurant.getString("place_id"),"RestaurantName");

                    //JSONObject openHours = jsonObjectRestaurant.getJSONObject("opening_hours");
                    //restaurant.setOpenNow(openHours.getBoolean("open_now"));
                    //Log.d(Boolean.toString(openHours.getBoolean("open_now")),"Is open now?");

                    restaurantList.add(restaurant);


                } catch (JSONException exception) {
                    valueIsMissing = true;
                    exception.printStackTrace();
                }
            //    if (valueIsMissing) {
              //      resturantsMarkerOptions[p] = null;          //Det är denna som förstör. Tas den bort så kommer några till som inte fungerar
                //    Log.d(restaurantList.get(restaurantList.size()-1).getName(),"Dessa har trasiga markeringar"); //Hur kan de komma ut på kartan när de inte får nån markeroptions?
                //}

                //else {
                    Log.d("Placing marker", "PlaceName");
                    resturantsMarkerOptions[p] = new MarkerOptions().position(restaurantLocation).title(restaurantName).icon
                            (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet(restaurantVicinity);//Vi kan ha olika ikoner beroende på typ av restaurang eller nått sånt.
                //}
                markerCounter++;


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
                    counter++; //Counter ta bort? Ingen skillnad?
                }
                else{
                    counter--;
                    Log.d("nullvalue", "nullder");
                }

            }


        }

    }

    public ArrayList getArrayList() {
        return restaurantList;
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









