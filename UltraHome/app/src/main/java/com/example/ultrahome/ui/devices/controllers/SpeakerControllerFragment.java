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

public class SpeakerControllerFragment extends Fragment {

    private Button buttonDeleteDevice;
    private String deviceId;
    private int positionInRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speaker_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readBundle(getArguments());

        buttonDeleteDevice = view.findViewById(R.id.button_delete_speaker);
        buttonDeleteDevice.setOnClickListener(this::deleteDevice);
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
    public static SpeakerControllerFragment newInstance(String deviceId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        SpeakerControllerFragment fragment = new SpeakerControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}
