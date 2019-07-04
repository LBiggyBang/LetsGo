package com.example.bbessaud.letsgo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;

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
    private float zoomLevel;

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
        }

        user = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SeekBar circleRadius = findViewById(R.id.radiusSeekBar);
        circleRadius.setMax(99);
        circleRadius.setProgress(0);

        circleRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                circle.setRadius(50*progress + 50);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, getZoomLevel(circle)));
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
                .fillColor(Color.BLUE)
                .strokeColor(Color.WHITE)
                .strokeWidth(0.5f)
                .radius(50)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, getZoomLevel(circle)));
    }

    public float getZoomLevel(Circle circle) {
        if (circle != null){
            float radius = (float) circle.getRadius();
            float scale = radius / 500;
            zoomLevel = (float) (14 - Math.log(scale) / Math.log(2.0));
        }
        return zoomLevel;
    }
}
