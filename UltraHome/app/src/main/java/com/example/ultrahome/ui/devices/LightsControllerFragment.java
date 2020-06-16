package com.example.ultrahome.ui.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lights_controller, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onOffSwitch = view.findViewById(R.id.lights_switch);
        onOffText = view.findViewById(R.id.lights_state_text);
        brightnessSeekBar = view.findViewById(R.id.brightness_seekbar);
        colorSelector = view.findViewById(R.id.lights_color_selector);

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

    private void updateState() {
        changeTextDependingOnState();
    }

    private void changeTextDependingOnState() {
        if(onOffSwitch.isChecked()) {
            onOffText.setText("ON");
        } else {
            onOffText.setText("OFF");
        }
    }
}
