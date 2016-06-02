package com.example.henrik.googlemapsexample.mapview;

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

public class MainActivity extends FragmentActivity implements LocationListener, SensorEventListener {
    private GoogleMap map;
    private boolean mapTypeChanger = true;
    private Marker[] resturantMarkers;
    private ArrayList<Restaurant> restaurantList = new ArrayList();
    private MarkerOptions[] resturantsMarkerOptions;
    private String nextPageToken;

    private boolean positionCheckOnce = true;
    private boolean restaurantDetail = true;
    private String restaurantDetailHTML;
    private int savedObjectId;
    private SlidingUpPanelLayout restaurantViewSlidePanel;
    private Restaurant restaurantTemp;
    private boolean databaseIsOnline = false;

    private boolean markerHiding = false;
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

    private int sortChooser = 0;
    private int numberOfHTMLpages = 0;

    private SharedPreferences preferences;
    private String SAVED_INFO = "savedFilters";

    private ArrayList<String> restaurantFilters = new ArrayList();
    private ArrayList<String> restaurantFiltersSorted = new ArrayList();
    private ArrayList<String> restaurantFilteredID = new ArrayList();
    private ArrayList<Restaurant> filteredRestaurantList = new ArrayList();

    private boolean resume = false;
    private boolean firstResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resturantMarkers = new Marker[60];
        setUserLocationOnMap();
        if (!DataStorage.getInstance().isUserPositionSupport()) {
            LatLng kristianstadCords = new LatLng(56.0333333, 14.1333333);
            zoomMapToMarker(kristianstadCords, 12);
        }
        sortButton = (Button) findViewById(R.id.sortButton);
        sortButton.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(restaurantList);
        prefsEditor.putString("ResturantObjectList", json);
        prefsEditor.commit();
        setUpAccelerometer();
        restaurantViewSlidePanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        restaurantViewListView = (ListView) findViewById(R.id.restaurantList);

        setUpRestaurantFilterArray();
        getfilteredID(restaurantFiltersSorted);

        firstTimeStart = false;
    }

    private void filterRestaurantList() {
        for (int i = 0; i < restaurantFilteredID.size(); i++) {
            for (int h = 0; h < restaurantList.size(); h++) {
                if (restaurantList.get(h).getId().equals(restaurantFilteredID.get(i))) {
                    filteredRestaurantList.add(restaurantList.get(h));
                }
            }
        }
    }

    private void getfilteredID(ArrayList filterOptions) {
        if (restaurantFiltersSorted.size() != 0 && restaurantFiltersSorted.size() != 15) {
            DatabaseHandler handler = new DatabaseHandler(this);
            handler.getRestaurantWithFilter(filterOptions, new DatabaseHandler.callbackGetRestaurantWithFilter() {
                @Override
                public void onSuccess(ArrayList<String> restaurantList) {
                    databaseIsOnline = true;
                    for (int i = 0; i < restaurantList.size(); i++) {
                        if (!restaurantFilteredID.contains(restaurantList.get(i)))
                            restaurantFilteredID.add(restaurantList.get(i));
                    }
                    if (resume) {
                        filterRestaurantList();
                        hideFilteredMarkersFromMap();
                        sortRestarantList("Sorted on Names", 4);

                    }

                }


            });
            if (databaseIsOnline == false) {
                //For future implementation
            }
        } else if (firstResume == true && restaurantFiltersSorted.size() == 0) {
            for (int i = 0; i < restaurantList.size(); i++) {
                restaurantList.get(i).getMarker().setVisible(false);
            }
        } else if (firstResume == true && restaurantFiltersSorted.size() == 15) {
            filteredRestaurantList = restaurantList;
            hideFilteredMarkersFromMap();
            sortRestarantList("Sorted on Names", 4);
        }


    }

    private void setUpRestaurantFilterArray() {
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

        for (int i = 0; i < 15; i++) {
            if (preferences.getBoolean(restaurantFilters.get(i), true)) {
                restaurantFiltersSorted.add(restaurantFilters.get(i));
            }
        }
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
    protected void onResume() {
        super.onResume();

        if (firstTimeStart && DataStorage.getInstance().isFilterIsActive()) {
            firstResume = true;
            resume = true;
            restaurantFilters.clear();
            restaurantFiltersSorted.clear();
            restaurantFilteredID.clear();
            filteredRestaurantList.clear();

            restaurantList = DataStorage.getInstance().getRestaurantList();

            for (int i = 0; i < restaurantList.size(); i++) {
                restaurantList.get(i).getMarker().setVisible(true);
            }
            setUpRestaurantFilterArray();
            getfilteredID(restaurantFiltersSorted);
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
        if (markerHiding) {
            int maxValue = filteredRestaurantList.size() - 1;
            int minValue = 0;
            int difference = maxValue - minValue;
            Random random = new Random();
            int result = random.nextInt(difference + 1);
            result += minValue;
            zoomMapToMarker(filteredRestaurantList.get(result).getPosition(), 18);
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
                        zoomMapToMarker(filteredRestaurantList.get(i).getPosition(), 18);
                        previusClickedID = i;
                    }


                }
            }
        });
        getRestaurantDistances();
        filterRestaurantList();
        hideFilteredMarkersFromMap();

        restaurantAdapter = new RestaurantAdapter(this, filteredRestaurantList);
        restaurantViewSlidePanel.setScrollableView(restaurantViewListView);
        restaurantViewListView.setAdapter(restaurantAdapter);

    }

    private void hideFilteredMarkersFromMap() {
        if (restaurantFiltersSorted.size() != 15) {
            boolean removeMarker;
            for (int i = 0; i < restaurantList.size(); i++) {
                removeMarker = true;
                for (int h = 0; h < filteredRestaurantList.size(); h++) {
                    if (restaurantList.get(i).getName().equals(filteredRestaurantList.get(h).getName())) {
                        removeMarker = false;
                        restaurantList.get(i).getMarker().setVisible(true);
                    }
                }
                if (removeMarker) {
                    restaurantList.get(i).getMarker().setVisible(false);
                }
            }
            //Better but not tested code:
           /* for(int i=0;i<restaurantList.size();i++){
                if(!filteredRestaurantList.contains(restaurantList.get(i))){
                    restaurantList.get(i).getMarker().setVisible(false);
                }
                else{
                    restaurantList.get(i).getMarker().setVisible(true);
                }
            }*/
        } else {
            filteredRestaurantList = restaurantList;
            for (int i = 0; i < restaurantList.size(); i++) {
                restaurantList.get(i).getMarker().setVisible(true);
            }
        }
        markerHiding = true;
    }

    public void onSortButtonClicked(View view) {
        sortChooser++;
        if (sortChooser == 1) {
            sortRestarantList("Sorted on review", sortChooser);
            Collections.reverse(filteredRestaurantList);
        } else if (sortChooser == 2)
            sortRestarantList("Sorted on distance", sortChooser);
        else {
            sortRestarantList("Sorted on name", sortChooser);
            sortChooser = 0;
        }
    }

    private void sortRestarantList(String buttonMessage, final int sortType) {
        Collections.sort(filteredRestaurantList, new Comparator<Restaurant>() {
            public int compare(Restaurant leftRestaurant, Restaurant rightRestaurant) {
                if (sortType == 1)
                    return leftRestaurant.getGoogleRating().compareTo(rightRestaurant.getGoogleRating());
                else if (sortType == 2)
                    return leftRestaurant.getDistanceToRestaurant().compareTo(rightRestaurant.getDistanceToRestaurant());
                else
                    return leftRestaurant.getName().compareTo(rightRestaurant.getName());
            }
        });
        if (sortType != 5)
            clearRestaurantListViewAndRefreshData(buttonMessage);
    }

    private void clearRestaurantListViewAndRefreshData(String textForButton) {
        ArrayList<Restaurant> restaurantTempArray = new ArrayList();

        for (int i = 0; i < filteredRestaurantList.size(); i++) {
            restaurantTempArray.add(filteredRestaurantList.get(i));
        }

        restaurantAdapter.clear();
        restaurantAdapter.addAll(restaurantTempArray);
        restaurantAdapter.notifyDataSetChanged();
        sortButton.setText(textForButton);
    }


    private void getRestaurantDistances() {
        Location userLocation = new Location("userLocation");
        if (DataStorage.getInstance().isUserPositionSupport()) {
            userLocation.setLatitude(DataStorage.getInstance().getUserPosition().latitude);
            userLocation.setLongitude(DataStorage.getInstance().getUserPosition().longitude);
        } else {
            userLocation.setLatitude(56.0333333);
            userLocation.setLongitude(14.1333333);
        }
        Location restaurantLocation = new Location("restaurantLocation");

        for (int i = 0; i < restaurantList.size(); i++) {
            restaurantLocation.setLatitude(restaurantList.get(i).getPosition().latitude);
            restaurantLocation.setLongitude(restaurantList.get(i).getPosition().longitude);
            float distance = userLocation.distanceTo(restaurantLocation);
            restaurantList.get(i).setDistanceToRestaurant(distance);
        }
    }

    public void panelListener() {
        restaurantViewSlidePanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) { //For future implementation

            }

            @Override
            public void onPanelExpanded(View panel) {
                sortButton.setActivated(true);
                sortButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                sortButton.setVisibility(View.GONE);
            }

            @Override
            public void onPanelAnchored(View panel) { //For future implementation

            }

            @Override
            public void onPanelHidden(View panel) { //For future implementation

            }
        });
    }


    private void setUserLocationOnMap() {
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

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()//Denna metod tar dock bort så att namnet inte längre syns när man klickar. Ha namnet alltid där? Eller bättre att bara erbjuda att öppna och inte tvinga användaren!
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0 != null) ;
                arg0.showInfoWindow();
                return true;
            }

        });

        String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/search/json?location=%2056.03129,14.15242&radius=10000&sensor=true&&types=restaurant&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE";

        if (positionCheckOnce) { //Will call everytime user change postion without boolean
            new GetRestaurantData().execute(googlePlacesUrl);
        }
        positionCheckOnce = false;
    }


    @Override
    public void onLocationChanged(Location location) {   //For future implementation

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { //For future implementation

    }

    @Override
    public void onProviderEnabled(String s) { //For future implementation

    }

    @Override
    public void onProviderDisabled(String s) { //For future implementation

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
        Intent intent = new Intent(MainActivity.this, Activity_FilterMenu.class);
        startActivity(intent);
    }


    public class GetRestaurantData extends AsyncTask<String, Void, String> {

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

                    } else {
                        Log.d("Wrong status code. Fail", "Status Code");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return returnedTextBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (restaurantDetail) {
                parseJsonData(result);
                placeAllRestaurantMarkersOnMap();
                for (int i = savedArraySize; i < restaurantList.size(); i++) {
                    restaurantList.get(i).setMarker(resturantMarkers[i]);
                }

                try {
                    Thread.sleep(2400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String nextPageTokenURL = "https://maps.googleapis.com/maps/api/place/search/json?location=%2056.03129,14.15242&radius=10000&sensor=true&&types=restaurant&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE&pagetoken=" + nextPageToken;

                if (nextPageToken != null) {
                    savedArraySize = restaurantList.size();
                    new GetRestaurantData().execute(nextPageTokenURL);
                } else {
                    restaurantDetail = false;
                    setRestaurantListView();
                    sortRestarantList("Sorted on names", 4);
                    panelListener();
                    DataStorage.getInstance().setRestaurantList(restaurantList);
                }
            }

            if (numberOfHTMLpages < 3 && restaurantDetailActivated) {
                parseRestaurantDetails(result);
            }

        }

        private void parseRestaurantDetails(String result) {
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
                }

                restaurantList.get(savedObjectId).setOpenHoursArray(openHourArray);


            } catch (JSONException e) {

            } catch (Exception e) {

            }


        }

        private void parseJsonData(String result) {
            try {
                JSONObject jsonObjectResult = null;
                jsonObjectResult = new JSONObject(result);

                JSONArray jsonRestaurantArray = null;
                jsonRestaurantArray = jsonObjectResult.getJSONArray("results");
                resturantsMarkerOptions = new MarkerOptions[jsonRestaurantArray.length()];

                for (int p = 0; p < jsonRestaurantArray.length(); p++) {
                    boolean valueIsMissing = false;
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
                        restaurant.setPosition(restaurantLocation);
                        restaurant.setVicinity(restaurantVicinity);
                        restaurant.setId(jsonObjectRestaurant.getString("place_id"));

                        try {
                            restaurant.setGoogleRating(jsonObjectRestaurant.getString("rating"));
                        } catch (JSONException exception) {
                            restaurant.setGoogleRating("0");
                        }
                        try {
                            restaurant.setPriceLevel(jsonObjectRestaurant.getString("price_level"));
                        } catch (JSONException exception) {
                            restaurant.setPriceLevel("0");
                        }
                        try {
                            JSONObject openHours = jsonObjectRestaurant.getJSONObject("opening_hours");
                            restaurant.setOpenNow(openHours.getBoolean("open_now"));
                        } catch (JSONException exception) {
                            restaurant.setOpenNow(false);
                        }

                        restaurantTemp = restaurant;


                    } catch (JSONException exception) {
                        valueIsMissing = true;
                        exception.printStackTrace();
                    }
                    if (valueIsMissing) {

                    } else {
                        resturantsMarkerOptions[p] = new MarkerOptions().position(restaurantLocation).title(restaurantName).icon
                                (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet(restaurantVicinity);

                        restaurantList.add(restaurantTemp);
                    }

                }
                try {
                    nextPageToken = jsonObjectResult.getString("next_page_token");
                } catch (JSONException exception) {
                    nextPageToken = null;
                    exception.printStackTrace();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }

        private void placeAllRestaurantMarkersOnMap() {
            int counter = 0;
            if (resturantsMarkerOptions != null && resturantMarkers != null) {
                for (int i = 0; i < resturantsMarkerOptions.length; i++) {
                    if (resturantsMarkerOptions[i] != null) {
                        resturantMarkers[savedArraySize + counter] = map.addMarker(resturantsMarkerOptions[i]);
                        counter++;
                    } else {
                        counter--;
                    }

                }


            }

        }


    }
}








