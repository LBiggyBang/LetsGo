package com.example.bbessaud.letsgo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private static final String TAG = "MyAdapter";

    private List<HashMap<String, String>> mPlacesList;
    private String mDestinationType;
    private Context mContext;

    public MyAdapter(Context context, List<HashMap<String, String>> placesList, String destinationType) {
        mPlacesList = placesList;
        mDestinationType = destinationType;
        mContext = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView image;
        TextView textView;

        public MyViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            textView = v.findViewById(R.id.place);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        // Setting place picture
        switch (mDestinationType){
            case "restaurant" :
                holder.image.setImageResource(R.drawable.logo_eat);
                break;
            case "bar" :
                holder.image.setImageResource(R.drawable.logo_drink);
                break;
            case "point_of_interest" :
                holder.image.setImageResource(R.drawable.logo_activity);
                break;
        }

        holder.textView.setText(mPlacesList.get(position).get("place_name"));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPlacesList.size();
    }
}

