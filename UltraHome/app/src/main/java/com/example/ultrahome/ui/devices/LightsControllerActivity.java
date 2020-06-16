package com.example.ultrahome.ui.devices;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ultrahome.R;

public class LightsControllerActivity extends AppCompatActivity {

    private Switch onOffSwitch;
    private TextView onOffText;
    private SeekBar brightnessSeekBar;
    private CardView colorSelector;
    private int[] colorRGB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights_controller);

        onOffSwitch = findViewById(R.id.lights_switch);
        onOffText = findViewById(R.id.lights_state_text);
        brightnessSeekBar = findViewById(R.id.brightness_seekbar);
        colorSelector = findViewById(R.id.lights_color_selector);

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
                // Toast.makeText(LightsControllerActivity.this, "Seek bar progress is :" + progressChangedValue, Toast.LENGTH_SHORT).show();
            }
        });

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeTextDependingOnState();
            }
        });

        colorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ColorPickerDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
//                        .setTitle("ColorPicker Dialog")
//                        .setPreferenceName("MyColorPickerDialog")
//                        .setPositiveButton(getString(R.string.confirm),
//                                new ColorEnvelopeListener() {
//                                    @Override
//                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
//                                        setLayoutColor(envelope);
//                                    }
//                                })
//                        .setNegativeButton(getString(R.string.cancel),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                })
//                        .attachAlphaSlideBar(true) // default is true. If false, do not show the AlphaSlideBar.
//                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
//                        .show();
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
