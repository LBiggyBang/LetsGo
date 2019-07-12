package com.example.bbessaud.letsgo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1234;
    static final int SEARCH_DISTANCE_REQUEST = 1;
    static final int SEARCH_DESTINATION_REQUEST = 2;

    private Button eatButton;
    private Button drinkButton;
    private Button activityButton;
    private TextView radiusTextView;

    private Context mContext;
    private LocationManager mLocationManager;
    protected Location mLocation;
    private boolean isGpsStarted = false;

    public String destinationType;
    public int searchDistance = 50;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("MainActivity", "onCreate");
        mContext = getApplicationContext();
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission() && !isGpsStarted) {
            Log.i("MainActivity", "Starting GPS");
            startGPS();
        }
        setContentView(R.layout.activity_main);

        radiusTextView = findViewById(R.id.distanceTextView);

        // Activity type buttons
        eatButton = findViewById(R.id.eatButton);
        drinkButton = findViewById(R.id.drinkButton);
        activityButton = findViewById(R.id.activityButton);
        eatButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setPressed(true);
                drinkButton.setPressed(false);
                activityButton.setPressed(false);

                destinationType = "restaurant";
                return true;
            }
        });
        drinkButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setPressed(true);
                eatButton.setPressed(false);
                activityButton.setPressed(false);

                destinationType = "drink";
                return true;
            }
        });
        activityButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setPressed(true);
                eatButton.setPressed(false);
                drinkButton.setPressed(false);

                destinationType = "activity";
                return true;
            }
        });

        // Get radius button
        Button getRadiusButton = findViewById(R.id.getRadiusButton);
        getRadiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity", "Get radius button clicked, going to map activity");
                Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);

                Bundle mapExtras = new Bundle();
                mapExtras.putParcelable("userLocation", mLocation);
                mapExtras.putInt("searchDistance", searchDistance);

                mapIntent.putExtras(mapExtras);

                startActivityForResult(mapIntent, SEARCH_DISTANCE_REQUEST);
            }
        });

        // Search button
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != destinationType && mLocation != null){
                    Log.i("MainActivity", "Search button clicked, going to search activity");
                    Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);

                    Bundle searchExtras = new Bundle();
                    searchExtras.putString("destinationType", destinationType);
                    searchExtras.putInt("searchDistance", searchDistance);
                    searchExtras.putParcelable("userLocation", mLocation);

                    searchIntent.putExtras(searchExtras);

                    startActivityForResult(searchIntent, SEARCH_DESTINATION_REQUEST);
                }
            }
        });
    }

    public void startGPS(){
        String perm = Manifest.permission.ACCESS_FINE_LOCATION;
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, perm);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            isGpsStarted = true;
            Log.i("MainActivity", "GPS started");
        }
        else{
            Log.i("MainActivity", "GPS not started due to lack of permission");
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("MainActivity", "Provider disabled");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Check which request we're responding to
        if (requestCode == SEARCH_DISTANCE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                searchDistance = resultIntent.getExtras().getInt("searchRadius");
                if (searchDistance >= 1000) {
                    radiusTextView.setText(Integer.toString(searchDistance/1000)+" km");
                } else {
                    radiusTextView.setText(Integer.toString(searchDistance)+" m");
                }
            }
        } else if (requestCode == SEARCH_DESTINATION_REQUEST) {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    Context context = getApplicationContext();
                    CharSequence text = "No result in defined search range";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
        }
    }
}