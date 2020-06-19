package com.example.ultrahome.ui.homes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.ultrahome.R;

import java.util.List;

public class HomesAdapterLinear extends HomesAdapter {

    public HomesAdapterLinear(Context context, List<String> namesList, HomesFragment currentFragment) {
        super(context, namesList, currentFragment);
    }

    @NonNull
    @Override
    public HomesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.home_row_layout, parent, false);
        return new HomesViewHolder(v);
    }
}
