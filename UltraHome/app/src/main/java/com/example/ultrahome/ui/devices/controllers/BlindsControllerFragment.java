package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.ui.devices.DevicesListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BlindsControllerFragment extends Fragment {

    private Button deleteButton;
    private FloatingActionButton editButton;
    private FloatingActionButton doneButton;

    private String deviceId;
    private int positionInRecyclerView;

    private boolean editing = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blinds_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readBundle(getArguments());

        deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this::deleteDevice);
        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setClickable(false);

        doneButton = view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(this::editPressed);
        doneButton.setVisibility(View.INVISIBLE);
        doneButton.setClickable(false);

        editButton = view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(this::editPressed);


    }

    private void editPressed(View view) {
        if (editing) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setClickable(true);
            deleteButton.setVisibility(View.INVISIBLE);
            deleteButton.setClickable(false);
            doneButton.setVisibility(View.INVISIBLE);
            doneButton.setClickable(false);
        } else {
            editButton.setVisibility(View.INVISIBLE);
            editButton.setClickable(false);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setClickable(true);
            doneButton.setVisibility(View.VISIBLE);
            doneButton.setClickable(true);
        }
        editing = !editing;
    }

    private void deleteDevice(View view) {
        DevicesListFragment containerFragment = (DevicesListFragment) getParentFragment();
        assert containerFragment != null;
        containerFragment.deleteDevice(view, positionInRecyclerView);
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
        }
    }

    @NonNull
    public static BlindsControllerFragment newInstance(String deviceId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        BlindsControllerFragment fragment = new BlindsControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}
