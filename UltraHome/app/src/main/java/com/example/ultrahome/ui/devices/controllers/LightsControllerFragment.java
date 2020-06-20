package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;

public class LightsControllerFragment extends Fragment {

    private Switch onOffSwitch;
    private TextView onOffText;
    private SeekBar brightnessSeekBar;
    private CardView colorSelector;
    private int[] colorRGB;
    private String deviceId;
    private int positionInRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lights_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onOffSwitch = view.findViewById(R.id.lights_switch);
        onOffText = view.findViewById(R.id.lights_state_text);
        brightnessSeekBar = view.findViewById(R.id.brightness_seekbar);
        colorSelector = view.findViewById(R.id.lights_color_selector);

        readBundle(getArguments());

        // todo: this should look for data in the DataBase !!!!!
        updateState();

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // nothing
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // todo: call DataBase and update brightness !!!
            }
        });

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeTextDependingOnState();
            }
        });
    }

    // HARDCODEADO
    private void updateState() {
        changeTextDependingOnState();
    }

    // HARDCODEADO
    private void changeTextDependingOnState() {
        if(onOffSwitch.isChecked()) {
            onOffText.setText("ON");
        } else {
            onOffText.setText("OFF");
        }
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
        }
    }

    @NonNull
    public static LightsControllerFragment newInstance(String deviceId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        LightsControllerFragment fragment = new LightsControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}
