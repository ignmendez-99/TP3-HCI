package com.example.ultrahome.ui.rooms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.ultrahome.R;

import java.util.List;

public class RoomsAdapterGrid extends RoomsAdapter {

    public RoomsAdapterGrid(Context context, List<String> namesList, RoomsFragment currentFragment) {
        super(context, namesList, currentFragment);
    }

    @NonNull
    @Override
    public RoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.room_grid_layout, parent, false);
        return new RoomsAdapter.RoomsViewHolder(v);
    }
}
