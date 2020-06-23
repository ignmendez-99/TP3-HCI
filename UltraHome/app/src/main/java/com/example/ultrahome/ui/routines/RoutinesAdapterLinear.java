package com.example.ultrahome.ui.routines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.ultrahome.R;

import java.util.List;

public class RoutinesAdapterLinear extends RoutinesAdapter {

    public RoutinesAdapterLinear(Context context, List<String> namesList, RoutinesFragment currentFragment) {
        super(context, namesList, currentFragment);
    }

    @NonNull
    @Override
    public RoutinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.routine_row_layout, parent, false);
        return new RoutinesAdapter.RoutinesViewHolder(v);
    }
}