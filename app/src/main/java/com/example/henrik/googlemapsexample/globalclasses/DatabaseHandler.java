package com.example.henrik.googlemapsexample.globalclasses;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.henrik.googlemapsexample.review.ReviewObject;
import com.example.henrik.googlemapsexample.userprofile.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by Nicklas on 2016-05-23.
 */
public class DatabaseHandler {

    private Context context;

    //Jocke hemma iP = 192.168.1.83

    String IP = "194.47.41.188";

    public DatabaseHandler(Context context){
        this.context = context;
    }



    public void getUserData(String device_id, final callbackGetUserData callback){
        String USER_URL = "http://" +  IP + "/android_connect/readUser.php?device_id=" + device_id;
        StringRequest stringRequest = new StringRequest(USER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                User user;
                String resultName = "";
                String resultDevice_id = "";
                String resultLikes = "";
                String resultDislikes = "";

                try {
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));  //response normalt
                    JSONArray result = jsonObject.getJSONArray("result");
                    int numberOfResultsGathered = 0;

                    for(int i = 0; i < result.length(); i++){
                        JSONObject userData = result.getJSONObject(numberOfResultsGathered);
                        resultName = userData.getString("name");
                        resultDevice_id = userData.getString("device_id");
                        resultLikes = userData.getString("likes");
                        resultDislikes = userData.getString("dislikes");
                        user = new User(resultDevice_id, resultName, Integer.parseInt(resultLikes), Integer.parseInt(resultDislikes));
                        numberOfResultsGathered++;

                        callback.onSuccess(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public interface VolleyCallback{
        void onSuccess(User user);
    }

    public interface callbackGetUserData {
        void onSuccess(User user);
    }


    public void addReview(String comment, int staff, int affordable, int quality, int ambience, String timestamp, String restaurant_id, String user_device_id){
        float avarage = (staff + affordable + quality + ambience)/4;


        String REVIEW_URL = "http://" +  IP + "/android_connect/addReview.php";

        RequestQueue queue;
        JsonObjectRequest request;

        queue = Volley.newRequestQueue(context);

        Map<String,String> params = new HashMap<String, String>();
        params.put("comment", comment);
        params.put("avarage", Float.toString(avarage));
        params.put("staff", Integer.toString(staff));
        params.put("affordable", Integer.toString(affordable));
        params.put("quality", Integer.toString(quality));
        params.put("ambience", Integer.toString(ambience));
        params.put("timestamp", timestamp);
        params.put("likes", "0");
        params.put("dislikes", "0");
        params.put("restaurant_id", restaurant_id);
        params.put("user_device_id", user_device_id);


        request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                REVIEW_URL, // the URL
                new JSONObject(params), // the parameters for the php
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response){

                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley ERROR:-------", "------- " + error);
                    }
                });

        // executing the quere to get the json information
        queue.add(request);
    }

    public void addUser(String device_id, String name, final callbackAddUserComplete callback){
        String USER_URL = "http://" + IP + "/android_connect/adduser.php";  //192.168.1.83    194.47.40.206
        final String DEVICE_ID = "device_id";
        final String NAME = "name";
        final String LIKES = "likes";
        final String DISLIKES = "dislikes";

        RequestQueue queue;
        JsonObjectRequest request;

        queue = Volley.newRequestQueue(context);

        Map<String,String> params = new HashMap<String, String>();
        params.put(DEVICE_ID, device_id);
        params.put(NAME, name);
        params.put(LIKES, "0");
        params.put(DISLIKES, "0");

        request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                USER_URL, // the URL
                new JSONObject(params), // the parameters for the php
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response){
                        callback.onSuccess("");
                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("","" + error);
                        callback.onSuccess("");
                    }
                });
        queue.add(request);
    }

    public interface callbackAddUserComplete{
        void onSuccess(String complete);
    }


    public void getAllReviewsFromUser(String device_id, final callbackGetAllReviewsFromUser callback){

        String USER_URL = "http://" +  IP + "/android_connect/readAllReviewsFromUser.php?user_device_id=" + device_id;
        StringRequest stringRequest = new StringRequest(USER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ReviewObject review;
                String resultId = "";
                String resultComment = "";
                String resultAvarage = "";
                String resultStaff = "";
                String resultAffordable = "";
                String resultQuality = "";
                String resultAmbience = "";
                String resultTimestamp = "";
                String resultRestaurant_id = "";
                String resultUser_device_id = "";
                String resultLikes = "";
                String resultDislikes = "";
                ArrayList<ReviewObject> reviewList = new ArrayList<>();


                try {
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));  //response normalt
                    JSONArray result = jsonObject.getJSONArray("result");
                    int numberOfResultsGathered = 0;

                    Log.d("JSONarray Length","                    aaaaaaaaaaaaaaa " + result.length());

                    for(int i = 0; i < result.length(); i++){
                        JSONObject userData = result.getJSONObject(numberOfResultsGathered);
                        resultId = userData.getString("id");
                        resultComment = userData.getString("comment");
                        resultAvarage = userData.getString("avarage");
                        resultLikes = userData.getString("likes");
                        resultDislikes = userData.getString("dislikes");
                        resultStaff = userData.getString("staff");
                        resultAffordable = userData.getString("affordable");
                        resultQuality = userData.getString("quality");
                        resultAmbience = userData.getString("ambience");
                        resultTimestamp = userData.getString("timestamp");
                        resultRestaurant_id = userData.getString("restaurant_id");
                        resultUser_device_id = userData.getString("user_device_id");

                        review = (new ReviewObject(Integer.parseInt(resultId), Float.parseFloat(resultAvarage), Integer.parseInt(resultStaff), Integer.parseInt(resultAffordable), Integer.parseInt(resultAmbience), Integer.parseInt(resultQuality), Integer.parseInt(resultDislikes), Integer.parseInt(resultLikes), resultComment, resultTimestamp,resultUser_device_id, resultRestaurant_id));

                       numberOfResultsGathered++;

                        reviewList.add(review);

                        //callback.onSuccess(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callback.onSuccess(reviewList);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public interface callbackGetAllReviewsFromUser{
        void onSuccess(ArrayList<ReviewObject> reviewList);
    }


    public void getAllReviewsFromRestaurant(String restaurant_id, final callbackGetAllReviewsFromRestaurant callback){

        String USER_URL = "http://" +  IP + "/android_connect/readAllReviewsFromRestaurant.php?restaurant_id=" + restaurant_id;
        StringRequest stringRequest = new StringRequest(USER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ReviewObject review;
                String resultId = "";
                String resultComment = "";
                String resultAvarage = "";
                String resultStaff = "";
                String resultAffordable = "";
                String resultQuality = "";
                String resultAmbience = "";
                String resultTimestamp = "";
                String resultRestaurant_id = "";
                String resultUser_device_id = "";
                String resultLikes = "";
                String resultDislikes = "";
                ArrayList<ReviewObject> reviewList = new ArrayList<>();


                try {
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));  //response normalt
                    JSONArray result = jsonObject.getJSONArray("result");
                    int numberOfResultsGathered = 0;



                    Log.d("JSONarray Length","                    sddsds " + result.length());

                    for(int i = 0; i < result.length(); i++){
                        JSONObject userData = result.getJSONObject(numberOfResultsGathered);
                        resultId = userData.getString("id");
                        resultComment = userData.getString("comment");
                        resultAvarage = userData.getString("avarage");
                        resultLikes = userData.getString("likes");
                        resultDislikes = userData.getString("dislikes");
                        resultStaff = userData.getString("staff");
                        resultAffordable = userData.getString("affordable");
                        resultQuality = userData.getString("quality");
                        resultAmbience = userData.getString("ambience");
                        resultTimestamp = userData.getString("timestamp");
                        resultRestaurant_id = userData.getString("restaurant_id");
                        resultUser_device_id = userData.getString("user_device_id");

                        review = (new ReviewObject(Integer.parseInt(resultId), Float.parseFloat(resultAvarage), Integer.parseInt(resultStaff), Integer.parseInt(resultAffordable), Integer.parseInt(resultAmbience), Integer.parseInt(resultQuality), Integer.parseInt(resultDislikes), Integer.parseInt(resultLikes), resultComment, resultTimestamp,resultRestaurant_id,resultUser_device_id));
                        numberOfResultsGathered++;

                        reviewList.add(review);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callback.onSuccess(reviewList);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public interface callbackGetAllReviewsFromRestaurant{
        void onSuccess(ArrayList<ReviewObject> reviewList);
    }

    public void addLikeorDislike(String device_id, int review_id, int liked){     //liked = 1, disliked = 0
        String USER_URL = "http://" + IP + "/android_connect/updateReviewLikes.php";  //192.168.1.83    194.47.40.206

        RequestQueue queue;
        JsonObjectRequest request;

        queue = Volley.newRequestQueue(context);

        Map<String,String> params = new HashMap<String, String>();
        params.put("user_device_id", device_id);
        params.put("id", Integer.toString(review_id));
        params.put("liked", Integer.toString(liked));

        request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                USER_URL, // the URL
                new JSONObject(params), // the parameters for the php
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response){

                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(request);
    }

    public void getRestaurantWithFilter(ArrayList<String> filterList, final callbackGetRestaurantWithFilter callback){
        String sql = "filter_sort%=27" + filterList.get(0) + "%27";
        //String sql = "SELECT * FROM restaurant_has_filter WHERE filter_sort='" + filterList.get(0) + "'";
        for(int i = 1; i < filterList.size(); i++) {
            sql += "%20OR%20filter_sort=%27" + filterList.get(i) + "%27";
        }
        Log.d("SQL STRING:", "    dskndadsdqpqåwqååwåwqåq " + sql);
        String USER_URL = "http://" +  IP + "/android_connect/getRestaurantFilter.php?sql=" + sql;
        StringRequest stringRequest = new StringRequest(USER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                String restaurantID = "";
                String filter_sort = "";

                ArrayList<String> restaurantFilterList = new ArrayList<>();


                try {
                    Log.d("respone","      abbbbbbbbbbadasdasb           " + response);
                    //JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));//response normalt
                    Log.d("JSONOBJECT","                JSONOBJECT     " + jsonObject.toString());
                    JSONArray result = jsonObject.getJSONArray("result");
                    int numberOfResultsGathered = 0;
                    Log.d("JSONarray Length","                    d " + result.length());
                    for(int i = 0; i < result.length(); i++){
                        JSONObject userData = result.getJSONObject(numberOfResultsGathered);
                        restaurantID = userData.getString("restaurant_id");
                        //filter_sort = userData.getString("filter_sort");

                        numberOfResultsGathered++;
                        restaurantFilterList.add(restaurantID);
                        Log.d("sdassdasda","wdqwdqdqwd   " + numberOfResultsGathered);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callback.onSuccess(restaurantFilterList);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public interface callbackGetRestaurantWithFilter{
        void onSuccess(ArrayList<String> restaurantList);
    }


    public void getAllUserData(final callbackGetAllUserData callback){
        String USER_URL = "http://" +  IP + "/android_connect/readAllUsers.php";
        StringRequest stringRequest = new StringRequest(USER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                User user;
                ArrayList<User> userList = new ArrayList<>();
                String resultName = "";
                String resultDevice_id = "";
                try {
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));  //response normalt
                    JSONArray result = jsonObject.getJSONArray("result");
                    int numberOfResultsGathered = 0;
                    for(int i = 0; i < result.length(); i++){
                        JSONObject userData = result.getJSONObject(numberOfResultsGathered);
                        resultName = userData.getString("name");
                        resultDevice_id = userData.getString("device_id");
                        user = new User(resultDevice_id, resultName, 0, 0);
                        numberOfResultsGathered++;
                        userList.add(user);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(userList);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void getReviewAvarageFromRestaurant(String restaurant_id, final callbackGetReviewAvarageFromRestaurant callback){
        String USER_URL = "http://" +  IP + "/android_connect/getAvarageFromRestaurant.php?restaurant_id=" + restaurant_id;
        StringRequest stringRequest = new StringRequest(USER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultAvarage = "";
                ArrayList<Double> avarageList = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));  //response normalt
                    JSONArray result = jsonObject.getJSONArray("result");
                    int numberOfResultsGathered = 0;

                    Log.d("JSONarray Length","                    sddsds " + result.length());
                    for(int i = 0; i < result.length(); i++){
                        JSONObject userData = result.getJSONObject(numberOfResultsGathered);
                        resultAvarage = userData.getString("avarage");
                        avarageList.add(Double.parseDouble(resultAvarage));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                double avarageRating = 0;
                for(int i = 0; i < avarageList.size(); i++){
                    avarageRating += avarageList.get(i);
                }
                avarageRating /= avarageList.size();
                callback.onSuccess(avarageRating);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    public interface callbackGetReviewAvarageFromRestaurant{
        void onSuccess(double avarage);
    }

    public interface callbackGetAllUserData{
        void onSuccess(ArrayList<User> userList);
    }

}
