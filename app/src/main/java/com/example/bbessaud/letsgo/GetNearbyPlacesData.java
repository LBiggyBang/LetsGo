package com.example.bbessaud.letsgo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, List<HashMap<String, String>>> {

    private String googlePlacesData;

    public AsyncResponse delegate = null;

    String url;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... objects) {

        url = (String) objects[0];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<HashMap<String, String>> nearbyPlaceList;
        DataParser parser = new DataParser();
        Log.d("GET_NEARBY_PLACES_DATA", "Called parsing method");
        nearbyPlaceList = parser.parse(googlePlacesData);

        return nearbyPlaceList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> result) {
        delegate.processFinish(result);
    }
}