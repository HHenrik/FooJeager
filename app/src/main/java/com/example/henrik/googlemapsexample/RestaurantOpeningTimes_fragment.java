package com.example.henrik.googlemapsexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Henrik on 2016-05-19.
 */
public class RestaurantOpeningTimes_fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.restaurant_opening_times, container, false);
       setListViewData(v);
        return v;
    }
    private void setListViewData(View v){
        ListView openingTimesList = (ListView) v.findViewById(R.id.openingHoursList);
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1,DataStorage.getInstance().getRestaurantList().get(DataStorage.getInstance().getActiveRestaurant()).getOpenHoursArray());
        openingTimesList.setAdapter(arrayAdapter);
    }
}


