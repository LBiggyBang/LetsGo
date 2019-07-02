package com.example.bbessaud.letsgo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1234;

    private Context mContext;
    private LocationManager mLocationManager;
    protected Location mLocation;
    private boolean isGpsStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission() && !isGpsStarted) {
            startGPS();
        }
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.location);
        Button button = findViewById(R.id.getLocation);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textView.setText(mLocation.toString());
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

    }
}
