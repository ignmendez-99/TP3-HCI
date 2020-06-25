package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.lights.LightState;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LightsControllerFragment extends Fragment {

    private Switch onOffSwitch;
    private SeekBar brightnessSeekBar;
    private TextView brightnessTextView;
    private Button redBtn, greenBtn, blueBtn, yellowBtn, warmWhiteBtn, purpleBtn, darkGreenBtn, orangeBtn, violetBtn, coolWhiteBtn, whiteBtn, lightGreyBtn, greyBtn, darkGreyBtn, blackBtn;
    private Button colorDisplay;

    private ApiClient api;

    private int currentBrightness;
    private boolean isOn, firstTime = true;
    private String currentColor;


    private String deviceId;

    private Map<String, Integer> colors;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lights_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        init(view);
    }

    private void initializeColorButtons(@NonNull View view) {
        redBtn = view.findViewById(R.id.red_color_button);
        greenBtn = view.findViewById(R.id.green_color_button);
        blueBtn = view.findViewById(R.id.blue_color_button);
        yellowBtn = view.findViewById(R.id.yellow_color_button);
        warmWhiteBtn = view.findViewById(R.id.warmWhite_color_button);
        purpleBtn = view.findViewById(R.id.purple_color_button);
        darkGreenBtn = view.findViewById(R.id.darkGreen_color_button);
        orangeBtn = view.findViewById(R.id.orange_color_button);
        violetBtn = view.findViewById(R.id.violet_color_button);
        coolWhiteBtn = view.findViewById(R.id.coolWhite_color_button);
        whiteBtn = view.findViewById(R.id.white_color_button);
        lightGreyBtn = view.findViewById(R.id.lighGrey_color_button);
        greyBtn = view.findViewById(R.id.grey_color_button);
        darkGreyBtn = view.findViewById(R.id.darkGrey_color_button);
        blackBtn = view.findViewById(R.id.black_color_button);

        redBtn.setOnClickListener(v -> changeColor(v, "#F44336"));
        greenBtn.setOnClickListener(v -> changeColor(v, "#4CFF00"));
        blueBtn.setOnClickListener(v -> changeColor(v, "#0036FF"));
        yellowBtn.setOnClickListener(v -> changeColor(v, "#FFDD00"));
        warmWhiteBtn.setOnClickListener(v -> changeColor(v, "#FFFADA"));
        purpleBtn.setOnClickListener(v -> changeColor(v, "#9C27B0"));
        darkGreenBtn.setOnClickListener(v -> changeColor(v, "#39813C"));
        orangeBtn.setOnClickListener(v -> changeColor(v, "#FF9800"));
        violetBtn.setOnClickListener(v -> changeColor(v, "#673AB7"));
        coolWhiteBtn.setOnClickListener(v -> changeColor(v, "#E9F8FF"));
        whiteBtn.setOnClickListener(v -> changeColor(v, "#FFFFFF"));
        lightGreyBtn.setOnClickListener(v -> changeColor(v, "#D8D8D8"));
        greyBtn.setOnClickListener(v -> changeColor(v, "#939393"));
        darkGreyBtn.setOnClickListener(v -> changeColor(v, "#4E4E4E"));
        blackBtn.setOnClickListener(v -> changeColor(v, "#000000"));

        colors = new HashMap<>();

        colors.put("#F44336", 0xFFF44336);
        colors.put("#4CFF00", 0xFF4CFF00);
        colors.put("#0036FF", 0xFF0036FF);
        colors.put("#FFDD00", 0xFFFFDD00);
        colors.put("#FFFADA", 0xFFFFFADA);
        colors.put("#9C27B0", 0xFF9C27B0);
        colors.put("#39813C", 0xFF39813C);
        colors.put("#FF9800", 0xFFFF9800);
        colors.put("#673AB7", 0xFF673AB7);
        colors.put("#E9F8FF", 0xFFE9F8FF);
        colors.put("#FFFFFF", 0xFFFFFFFF);
        colors.put("#D8D8D8", 0xFFD8D8D8);
        colors.put("#939393", 0xFF939393);
        colors.put("#4E4E4E", 0xFF4E4E4E);
        colors.put("#000000", 0xFF000000);
    }

    private void init(View view) {
        initializeColorButtons(view);

        onOffSwitch = view.findViewById(R.id.onOff_Switch);
        brightnessSeekBar = view.findViewById(R.id.brightness_seekbar);
        brightnessTextView = view.findViewById(R.id.brightness_textView);

        colorDisplay = view.findViewById(R.id.color_show);

        api = ApiClient.getInstance();

        api.getLightState(deviceId, new Callback<Result<LightState>>() {
            @Override
            public void onResponse(@NonNull Call<Result<LightState>> call, @NonNull Response<Result<LightState>> response) {
                if(response.isSuccessful()) {
                    Result<LightState> result = response.body();
                    if(result != null) {
                        LightState lightState = result.getResult();

                        currentBrightness = lightState.getBrightness();
                        brightnessTextView.setText(currentBrightness + "%");
                        brightnessSeekBar.setProgress(currentBrightness);

                        isOn = lightState.isOn();
                        onOffSwitch.setChecked(isOn);

                        currentColor = lightState.getColor();
                        colorDisplay.setBackgroundColor(colors.get(currentColor));

                        firstTime = false;
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<LightState>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), LightsControllerFragment.this);
            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!firstTime)
                    changeBrightness();
            }
        });

        onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!firstTime)
                turnOnOrOff();
        });
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
        }
    }

    @NonNull
    public static LightsControllerFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);

        LightsControllerFragment fragment = new LightsControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void changeColor(View view, String newColor) {
        if(currentColor == newColor)
            return;
        api.setLightColor(deviceId, newColor, new Callback<Result<String>>() {
            @Override
            public void onResponse(@NonNull Call<Result<String>> call, @NonNull Response<Result<String>> response) {
                if(response.isSuccessful()) {
                    Result<String> result = response.body();
                    if(result != null) {
                        currentColor = newColor;
                        colorDisplay.setBackgroundColor(colors.get(currentColor));
                        Toast.makeText(getContext(),getString(R.string.color_changed_string), Toast.LENGTH_SHORT).show();
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<String>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), LightsControllerFragment.this);
            }
        });

    }

    private void changeBrightness() {
        if(currentBrightness == brightnessSeekBar.getProgress())
            return;
        api.setLightBrightness(deviceId, brightnessSeekBar.getProgress(), new Callback<Result<Integer>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Integer>> call, @NonNull Response<Result<Integer>> response) {
                if(response.isSuccessful()) {
                    Result<Integer> result = response.body();
                    if(result != null) {
                        currentBrightness = brightnessSeekBar.getProgress();
                        brightnessTextView.setText(currentBrightness + "%");
                        Toast.makeText(getContext(), getString(R.string.intensity_set_to_string) + " " + brightnessSeekBar.getProgress(), Toast.LENGTH_SHORT).show();
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Integer>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), LightsControllerFragment.this);
            }
        });

    }

    private void turnOnOrOff() {
        if(!isOn) {
            api.turnOnLight(deviceId, new Callback<Result<Boolean>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                    if(response.isSuccessful()) {
                        Result<Boolean> result = response.body();
                        if(result != null) {
                            Toast.makeText(getContext(),getString(R.string.turned_on_string), Toast.LENGTH_SHORT).show();
                            isOn = true;
                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t, requireView(), LightsControllerFragment.this);
                }
            });
        } else {
            api.turnOffLight(deviceId, new Callback<Result<Boolean>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                    if(response.isSuccessful()) {
                        Result<Boolean> result = response.body();
                        if(result != null) {
                            Toast.makeText(getContext(),getString(R.string.turned_off_string), Toast.LENGTH_SHORT).show();
                            isOn = false;
                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t, requireView(), LightsControllerFragment.this);
                }
            });
        }

    }


}
