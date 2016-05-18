package com.example.henrik.googlemapsexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

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

public class MainActivity extends FragmentActivity implements LocationListener {
    private GoogleMap map;
    private boolean mapTypeChanger = true;
    private Marker[] resturantMarkers;
    private ArrayList <Restaurants> restaurantsList = new ArrayList();
    private MarkerOptions[] resturantsMarkerOptions;
    private String nextPageToken;
    private boolean oncePage1 = true;
    private boolean testbBoolean = true;
    private boolean restaurantDetail = true;
    private String restaurantDetailHTML;
    private int savedObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resturantMarkers = new Marker[60];//Max antal restauranger som visas
       setUserLocationOnMap();


        Context context = getApplicationContext();
        SharedPreferences  sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(restaurantsList);
        prefsEditor.putString("ResturantObjectList", json);
        prefsEditor.commit();




      /*  GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .build();
*/

    }

    private void removeIndividualMarker(String restaurantName){
        for(int i=0;i<restaurantsList.size();i++){
            if(restaurantsList.get(i).getName().equals(restaurantName)){
                restaurantsList.get(i).getMarker().setVisible(false);
            }


    }}
    private void setUserLocationOnMap() {
            final double tesLat = 56.03129;
            final double testLong = 14.15242;
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            map.setMyLocationEnabled(true);
            GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    DataStorage.getInstance().setUserPostion(new LatLng(location.getLatitude(),location.getLongitude()));
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
                new GoogleMap.OnInfoWindowClickListener(){
                    public void onInfoWindowClick(Marker marker){
                        DataStorage.getInstance().setActiveRestaurant(marker.getTitle());
                        for(int i=0;i<restaurantsList.size();i++){
                            if(restaurantsList.get(i).getName().equals(marker.getTitle())){
                                restaurantDetailHTML = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+restaurantsList.get(i).getId()+"&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE";
                                savedObjectId = i;
                            }
                        }
                        new GetRestaurantData().execute(restaurantDetailHTML);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        DataStorage.getInstance().setRestaurantsList(restaurantsList);
                        Log.d("infoClick","openAc");
                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", marker.getTitle());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );




        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()//Denna metod tar dock bort så att namnet inte längre syns när man klickar. Ha namnet alltid där? Eller bättre att bara erbjuda att öppna och inte tvinga användaren!
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                if(arg0 != null);
                arg0.showInfoWindow();
                //Här ska man anropa istället på en activty eller fragment där restaurangen ploppar upp med information.
                //restaurantsList.get(0).getMarker().remove();
                        Log.d(restaurantsList.get(0).getName() + "  got removed","se");
                Log.d(arg0.getTitle(),"PlaceName");//Här öppna resturangen
                return true;
            }

        });

        String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/search/json?location=%2056.03129,14.15242&radius=10000&sensor=true&&types=restaurant&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE";
        //Kordinater för Kristianstad. Kan byta till location.getLatitude(), location.getLongitude()
        if(testbBoolean) { //testBoolean tillfällig. Annars kommer den att anropas varje gång som användaren byter postion.
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
    public void mainMenuButton(View view){
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
    }
    public void filterMenuButton(View view){
        //Tillfälligt här. Använda removeindivdualmarker() istället vid filtering.
        //restaurantsList.get(0).getMarker().setVisible(false); //Antingen gömma undan eller ta bort helt. Om ta bort måste vi spara undan MarkerOptions[] i resturangobjektet också pga att den behövs för att skapa markeringen
        Intent intent = new Intent(MainActivity.this, Activity_FilterMenu.class);
        startActivity(intent);
    }




    public class GetRestaurantData extends AsyncTask<String, Void, String> { //Flytta ut till ett eget klassdokument

        @Override
        protected String doInBackground(String... googlePlacesURL) {
          //  Log.d(googlePlacesURL.toString(),"Se hit");
            StringBuilder returnedTextBuilder = new StringBuilder();

            for (String googlePlacesSearchURL : googlePlacesURL) {
                Log.d("Looping1","PlaceName");
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
                            Log.d("Looping2","PlaceName");
                        }

                        Log.d(returnedTextBuilder.toString(),"PlaceName");

                    }
                    else{
                        Log.d("Wrong status code. Fail","Status Code");
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            Log.d(returnedTextBuilder.toString(),"Placename");
            return returnedTextBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("On Post execute", "PlaceName");
          //  removeAllCurrentRestaurantMarkersFromMarkerArray();
            if(restaurantDetail) {


                parseJsonData(result);
                placeAllRestaurantMarkersOnMap();
                try {
                    Thread.sleep(1200);//värdet är inte optimerat för tillfället. Kan säkert vara lägre. Men behövs annars blir det skit av det...
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //testNextPage är kopirad i från browsern och fungerar garanterat.
                //  String testNextPage = "https://maps.googleapis.com/maps/api/place/search/json?location=%2056.03129,14.15242&radius=10000&sensor=true&&types=restaurant&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE&pagetoken=CoQC_wAAAC3Mixbn_e_Po3I631GeRnYcOuza1gRomm6BoVPwmOqstuRVe9vJ77Lt4jejxUjBe7TycbZCPbspYkoLl2226wF29zN0OtZWx6KpdDhQN1eq3aUAq1mezGwx9X26aytaBkZ4ev1b0vyJMjWkbUXO3xv1eolQOWyahqJPwDZVi5gWhlD5y8TDaR_0N4YT-jvUyxjk1lDWoL8vyW3XlTJBvs3AFzqLr6AVbgr9CyKrXKtD0pLf8cKcpy34v1R4Q5H7lSEE0Dn552fiDCEp_oDnOxNvfiMYOcHgIO4ndrj5wQ7GSk733P4AXebSv-DCEDZrS_gFTcgKxv5ePxXhSSHQdfkSEEjWK7pM_p9j04MHqWDGzqUaFLyoSNTt1f4w1Om002OQvuMMGW-u";
                String nextPageTokenURL = "https://maps.googleapis.com/maps/api/place/search/json?location=%2056.03129,14.15242&radius=10000&sensor=true&&types=restaurant&key=AIzaSyBwnl9UME858omHSWaF4U7LPKep6-ow1dE&pagetoken=" + nextPageToken;

                if (oncePage1) {
                    oncePage1 = false;
                    new GetRestaurantData().execute(nextPageTokenURL);
                } else {
                    for (int i = 0; i < restaurantsList.size(); i++) {  //Flyttat hit pga måste köras efter att vi fått hem samtliga pins
                        restaurantsList.get(i).setMarker(resturantMarkers[i]);
                    }
                    restaurantDetail = false;
                }
            }
            if(restaurantDetail == false) {
                    parseRestaurantDetails(result);
                }
                DataStorage.getInstance().setPlacedLoaded(true);
            }
        }
        private void parseRestaurantDetails(String result){//Fungerar

            Log.d("WOULD YOU LOOK AT THAT1","SeHIT");
            JSONObject jsonObjectResult = null;
            try {
                jsonObjectResult = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jObjectResult = (JSONObject) jsonObjectResult.get("result");

                restaurantsList.get(savedObjectId).setPhoneNumber(jObjectResult.getString("formatted_phone_number"));
                restaurantsList.get(savedObjectId).setWebsiteLink(jObjectResult.getString("website"));
                ArrayList <ReviewObject> reviewArray = new ArrayList();
                JSONArray reviews = jObjectResult.getJSONArray("reviews");
                for(int i=0;i<reviews.length();i++){
                    JSONObject reviewIndex = reviews.getJSONObject(i);
                    ReviewObject restaurantReviews = new ReviewObject(Float.parseFloat(reviewIndex.getString("rating")),reviewIndex.getString("author_name"),reviewIndex.getString("text"));
                    reviewArray.add(i,restaurantReviews);
                }
                DataStorage.getInstance().getRestaurantsList().get(savedObjectId).setReviews(reviewArray);
              /*  JSONObject jan = reviews.getJSONObject(0);

                String name = jan.getString("author_name");
                String text = jan.getString("text");
                String grade = jan.getString("rating");
                Log.d(name,"Thomas");
                Log.d(text,"Thomas");
                Log.d(grade,"Thomas");
*/
                //ArrayList <String> ove = new ArrayList();

               // JSONArray sven;
               // sven = jObjectResult.getJSONArray("reviews");
               // String Yngve = sven.getString(0);
                //Log.d(Yngve.toString(),"HassanLassan");
               // System.out.print(Yngve+"HassanLassan");




            } catch (JSONException e) {
            } catch (Exception e) {
            }

           // Log.d(result,"SeHIT");

            Log.d("WOULD YOU LOOK AT THAT2","SeHIT");






        }

        private void parseJsonData(String result){
                try {
                    int markerCounter = 0;
                    JSONObject jsonObjectResult = null;

                    jsonObjectResult = new JSONObject(result);

                    nextPageToken = jsonObjectResult.getString("next_page_token");
                    Log.d(nextPageToken, "Nextpagetest");
                    Log.d("Jonas", "Nextpagetest");


                    Log.d(nextPageToken, "Hasse");
                    Log.d("Lasse", "Hasse");


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

                            Log.d(restaurantName, "PlaceName");

                            Restaurants restaurants = new Restaurants();

                            restaurants.setGoogleRating(jsonObjectRestaurant.getString("rating"));
                            restaurants.setName(jsonObjectRestaurant.getString("name"));
                            // restaurants.setOpen_now(placeObject.getString("open_now"))
                            restaurants.setPosition(restaurantLocation);
                            restaurants.setVicinity(restaurantVicinity);
                            restaurants.setId(jsonObjectRestaurant.getString("place_id"));
                            restaurants.setPriceLevel("price_level");




                            //restaurants.setPhoneNumber(jsonObjectRestaurant.getInt("formatted_phone_number"));
                            //Log.d(restaurants.getGoogleRating(),"tittahär");fdfdb

                            restaurantsList.add(restaurants);


                            Log.d(restaurantsList.get(0).getName(), "Titta");


                        } catch (JSONException exception) {
                            valueIsMissing = true;
                            exception.printStackTrace();
                        }
                        if (valueIsMissing) {
                            resturantsMarkerOptions[p] = null;
                        } else {
                            Log.d("Placing marker", "PlaceName");
                            resturantsMarkerOptions[p] = new MarkerOptions().position(restaurantLocation).title(restaurantName).icon
                                    (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet(restaurantVicinity);//Vi kan ha olika ikoner beroende på typ av restaurang eller nått sånt.
                        }
                        markerCounter++;


                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }



            }



        private void removeAllCurrentRestaurantMarkersFromMarkerArray(){//Onödig om vi bara ska söka en gång med en statisk plats
            if (resturantMarkers != null) {
                for (int placedMarker = 0; placedMarker < resturantMarkers.length; placedMarker++) {
                    if (resturantMarkers[placedMarker] != null)
                        resturantMarkers[placedMarker].remove();
                }
            }
        }

        private void placeAllRestaurantMarkersOnMap() {
            if (resturantsMarkerOptions != null && resturantMarkers != null) {
                for (int i = 0; i < resturantsMarkerOptions.length && i < resturantMarkers.length; i++) {
                    if (resturantsMarkerOptions[i] != null) {
                        resturantMarkers[i] = map.addMarker(resturantsMarkerOptions[i]);
                        Log.d("Setting marker", "PlaceName");
                    }

                }


            }

        }
        public ArrayList getArrayList(){
            return restaurantsList;
        }

    
    private void hideRestaurantOnType(){
        for(int i = 0;i < restaurantsList.size();i++){
         //   if(){
                restaurantsList.get(i).getMarker().setVisible(false);
            }
      //      else{
               // restaurantsList.get(i).getMarker().setVisible(true);
            }

        }









