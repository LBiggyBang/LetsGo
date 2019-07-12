package com.example.bbessaud.letsgo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng user;
    private GoogleMap mMap;
    private Circle circle;
    private UiSettings mUiSettings;
    private Location mLocation;
    private int zoomLevel;
    private int searchRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Retrieving user location
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra("userLocation")) {
                mLocation = intent.getParcelableExtra("userLocation");
            }
            if (intent.hasExtra("searchDistance")) {
                searchRadius = intent.getIntExtra("searchDistance", 50);
            }
        }

        user = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SeekBar circleRadius = findViewById(R.id.radiusSeekBar);
        circleRadius.setMax(99);

        // Initialize seekbar, map and camera
        circleRadius.setProgress((searchRadius - 50)/50);

        final TextView radiusText = findViewById(R.id.radiusText);
        if (searchRadius >= 1000) {
            radiusText.setText(Integer.toString(searchRadius/1000)+" km");
        } else {
            radiusText.setText(Integer.toString(searchRadius)+" m");
        }

        circleRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                searchRadius = 50 * progress + 50;
                circle.setRadius(searchRadius);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, getZoomLevel(circle)));

                if (searchRadius >= 1000) {
                    radiusText.setText(Integer.toString(searchRadius/1000)+" km");
                } else {
                    radiusText.setText(Integer.toString(searchRadius)+" m");
                }
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        });

        Button validateRadius = findViewById(R.id.validateRadius);
        validateRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MapsActivity", "Validate radius button clicked, going to main activity");

                Intent resultIntent = new Intent();
                resultIntent.putExtra("searchRadius", searchRadius);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Locking the camera
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setAllGesturesEnabled(false);
        mUiSettings.setCompassEnabled(true); //TODO compass does not show up

        // Add a marker
        mMap.addMarker(new MarkerOptions()
                .position(user)
                .visible(false)
        );

        // Add its circle
        circle = mMap.addCircle(new CircleOptions()
                .center(user)
                .fillColor(0xAAAADAFF)
                .strokeColor(0xFF5683FF)
                .strokeWidth(2.5f)
                .radius(searchRadius)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, getZoomLevel(circle)));
    }

    public float getZoomLevel(Circle circle) {
        if (circle != null){
            float radius = (float) circle.getRadius();
            float scale = radius / 500;
            zoomLevel = (int) (15 - Math.log(scale) / Math.log(2.0));
            if (zoomLevel >= 16) {
                zoomLevel = 17;
            } else if (zoomLevel >= 14) {
                zoomLevel = 15;
            }
        }
        return zoomLevel;
    }
}
