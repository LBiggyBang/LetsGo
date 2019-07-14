package com.example.bbessaud.letsgo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultsActivity extends Activity {

    private static final String TAG = "ResultsActivity";

    private List<HashMap<String, String>> mPlacesList;
    private String destinationType;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        recyclerView = findViewById(R.id.my_recycler_view);

        // Retrieving results info
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra("destinationType")) {
                destinationType = intent.getStringExtra("destinationType");
            }
            if (intent.hasExtra("placesList")) {
                mPlacesList = (List<HashMap<String, String>>) intent.getSerializableExtra("placesList");
            }
        }


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new MyAdapter(this, mPlacesList, destinationType);
        recyclerView.setAdapter(mAdapter);
    }
}

