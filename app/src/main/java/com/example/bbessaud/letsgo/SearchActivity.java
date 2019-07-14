package com.example.bbessaud.letsgo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {

    private String destinationType;
    private int searchDistance;
    private Location mLocation;
    private List<HashMap<String, String>> placesList;
    private int placesNumber;
    private String placeId;

    private ConstraintLayout loadingLayout = null;
    private ConstraintLayout resultLayout = null;
    private TextView placeName;
    private TextView vicinity;
    private TextView openingHours;

    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Retrieving search info
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

        // Retrieving view layout
        loadingLayout = findViewById(R.id.loadingLayout);
        resultLayout = findViewById(R.id.resultLayout);
        ImageView placePicture = findViewById(R.id.placePicture);
        placeName = findViewById(R.id.placeName);
        vicinity = findViewById(R.id.vicinity);
        openingHours = findViewById(R.id.openingHours);
        Button letsGoButton = findViewById(R.id.letsGoButton);
        Button retryButton = findViewById(R.id.retryButton);
        Button resultsButton = findViewById(R.id.resultsButton);

        // Setting place picture
        switch (destinationType){
            case "restaurant" :
                placePicture.setImageResource(R.drawable.logo_eat);
                break;
            case "bar" :
                placePicture.setImageResource(R.drawable.logo_drink);
                break;
            case "point_of_interest" :
                placePicture.setImageResource(R.drawable.logo_activity);
                break;
        }

        letsGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SearchActivity", "Let's Go button clicked");

                Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=Eiffel%20Tower&query_place_id="+placeId);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SearchActivity", "Retry button clicked");

                loadingLayout.setVisibility(View.VISIBLE);
                resultLayout.setVisibility(View.GONE);

                int randomPlaceRank = new Random().nextInt(placesNumber);

                //placePicture
                placeName.setText(placesList.get(randomPlaceRank).get("place_name"));
                if ("" != placesList.get(randomPlaceRank).get("vicinity")) {
                    vicinity.setText(placesList.get(randomPlaceRank).get("vicinity"));
                } else {
                    vicinity.setText("Vicinity not given");
                }
                if (placesList.get(randomPlaceRank).get("opening_hours").contains("true")) {
                    openingHours.setText("Currently open");
                } else if (placesList.get(randomPlaceRank).get("opening_hours").contains("false")) {
                    openingHours.setText("Closed for now. You can check opening hours by clicking on Let's Go!");
                } else {
                    openingHours.setText("Opening hours not indicated");
                }
                placeId = placesList.get(randomPlaceRank).get("place_id");

                loadingLayout.setVisibility(View.GONE);
                resultLayout.setVisibility(View.VISIBLE);
            }
        });

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SearchActivity", "Results button clicked");

                Intent resultsIntent = new Intent(SearchActivity.this, ResultsActivity.class);

                Bundle resultsExtras = new Bundle();
                resultsExtras.putString("destinationType", destinationType);
                resultsExtras.putSerializable("placesList", (Serializable) placesList);

                resultsIntent.putExtras(resultsExtras);
                startActivity(resultsIntent);
            }
        });

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
    }

    @Override
    public void processFinish(List<HashMap<String, String>> output){

        placesList = output;

        // Preparing and displaying result
        placesNumber = placesList.size();

        // If there are no results
        if (placesNumber == 0) {
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        } else {
            int randomPlaceRank = new Random().nextInt(placesNumber);

            //placePicture
            placeName.setText(placesList.get(randomPlaceRank).get("place_name"));
            if ("" != placesList.get(randomPlaceRank).get("vicinity")) {
                vicinity.setText(placesList.get(randomPlaceRank).get("vicinity"));
            }
            if (placesList.get(randomPlaceRank).get("opening_hours").contains("true")) {
                openingHours.setText("Currently open");
            } else if (placesList.get(randomPlaceRank).get("opening_hours").contains("false")) {
                openingHours.setText("Closed for now. You can check opening hours by clicking on Let's Go!");
            }
            placeId = placesList.get(randomPlaceRank).get("place_id");
            loadingLayout.setVisibility(View.GONE);
            resultLayout.setVisibility(View.VISIBLE);
        }
    }
}
