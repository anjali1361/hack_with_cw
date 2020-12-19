package com.teamvoyager.collegeapp_voyager.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bitsindri.hncc.collegeapp.GetterAndSetter.googlemap;
import bitsindri.hncc.collegeapp.R;

public class GoogleMapAdapter extends RecyclerView.Adapter {

    ArrayList<googlemap> items;
    Context myContext;

    public GoogleMapAdapter(ArrayList<googlemap> items, Context myContext){
        this.items = items;
        this.myContext = myContext;

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_map_item, parent, false);
        return new GoogleMapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        bitsindri.hncc.collegeapp.Adapters.GoogleMapAdapter.GoogleMapViewHolder mapViewHolder = (bitsindri.hncc.collegeapp.Adapters.GoogleMapAdapter.GoogleMapViewHolder)holder;
        mapViewHolder.searches.setText(items.get(position).getSearches());



    }

    @Override
    public int getItemCount() {
        return items == null? 0: items.size();
    }

    public static class GoogleMapViewHolder extends RecyclerView.ViewHolder {

        TextView searches;

        public GoogleMapViewHolder(View itemView) {
            super(itemView);
             searches = itemView.findViewById(R.id.searches);

        }
    }
}
