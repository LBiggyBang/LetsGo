package com.example.bbessaud.letsgo;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {

    private String destinationType;
    private int searchDistance;
    private Location mLocation;
    private List<HashMap<String, String>> placesList;

    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Retrieve search info
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra("destinationType")) {
                destinationType = intent.getStringExtra("destinationType");
            }
            if (intent.hasExtra("searchDistance")) {
                searchDistance = intent.getIntExtra("searchDistance", 100);
            }
            if (intent.hasExtra("userLocation")) {
                mLocation = intent.getParcelableExtra("userLocation");
            }
        }

        // Creating a URL as a String
        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        urlString = urlString.concat(Double.toString(mLocation.getLatitude()));
        urlString = urlString.concat(",");
        urlString = urlString.concat(Double.toString(mLocation.getLongitude()));
        urlString = urlString.concat("&radius=");
        urlString = urlString.concat(Integer.toString(searchDistance));
        urlString = urlString.concat("&type=");
        urlString = urlString.concat(destinationType);
        urlString = urlString.concat("&key=");
        urlString = urlString.concat(getString(R.string.google_maps_key));
        Log.i("MainActivity", urlString);

        // Request a places search
        Object dataTransfer[] = new Object[1];

        getNearbyPlacesData.delegate = this;

        dataTransfer[0] = urlString;

        getNearbyPlacesData.execute(dataTransfer);

        // Preparing
    }

    @Override
    public void processFinish(List<HashMap<String, String>> output){

        placesList = output;
    }
}
