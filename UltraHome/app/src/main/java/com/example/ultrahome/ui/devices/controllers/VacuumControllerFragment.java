package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.vacuum.VacuumState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VacuumControllerFragment extends Fragment {

    private String deviceId;

    private Button startButton, pauseButton, dockButton;
    private Switch modeSwitch;
    private Spinner roomToCleanSpinner;
    private TextView batteryTextView;

    private String status, mode, location;
    private boolean runThreads = true;

    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vacuum_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        init(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        runThreads = false;
    }

    private void init(View view) {
        startButton = view.findViewById(R.id.start_button);
        pauseButton = view.findViewById(R.id.pause_button);
        dockButton = view.findViewById(R.id.dock_button);
        modeSwitch = view.findViewById(R.id.vacuumMop_switch);
        roomToCleanSpinner = view.findViewById(R.id.room_to_clean_spinner);
        batteryTextView = view.findViewById(R.id.battery_level);


        api = ApiClient.getInstance();

        api.getVacuumState(deviceId, new Callback<Result<VacuumState>>() {
            @Override
            public void onResponse(@NonNull Call<Result<VacuumState>> call, @NonNull Response<Result<VacuumState>> response) {
                if(response.isSuccessful()) {
                    Result<VacuumState> result = response.body();
                    if(result != null) {
                        VacuumState vacuumState = result.getResult();
                        status = vacuumState.getStatus();
                        mode = vacuumState.getMode();
//                        location = vacuumState.getLocation();
                        batteryTextView.setText(getString(R.string.battery_string) + " " + vacuumState.getBatteryLevel() + "%");
                        updateButtons();

                        if(mode.equals("vacuum"))
                            modeSwitch.setChecked(false);
                        else
                            modeSwitch.setChecked(true);



                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<VacuumState>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), VacuumControllerFragment.this);
            }
        });

        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                changeMode("mop");
            } else {
                changeMode("vacuum");
            }
        });


        startButton.setOnClickListener(v -> changeState(v, "start"));
        pauseButton.setOnClickListener(v -> changeState(v, "pause"));
        dockButton.setOnClickListener(v -> changeState(v, "dock"));

        startBatteryThread();

    }

    private void changeMode(String newMode) {
        if(newMode.equals(mode))
            return;

        api.setVacuumMode(deviceId, newMode, new Callback<Result<String>>() {
            @Override
            public void onResponse(@NonNull Call<Result<String>> call, @NonNull Response<Result<String>> response) {
                if(response.isSuccessful()) {
                    Result<String> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), getString(R.string.mode_changed_string), Toast.LENGTH_LONG).show();
                        mode = newMode;
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<String>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), VacuumControllerFragment.this);
            }
        });
    }

    private void changeState(View view, String newState) {
        if(newState.equals(status))
            return;

        if(newState.equals("start")) {
            api.getVacuumState(deviceId, new Callback<Result<VacuumState>>() {
                @Override
                public void onResponse(@NonNull Call<Result<VacuumState>> call, @NonNull Response<Result<VacuumState>> response) {
                    if(response.isSuccessful()) {
                        Result<VacuumState> result = response.body();
                        if(result != null) {
                            VacuumState vacuumState = result.getResult();
                            if(vacuumState.getBatteryLevel() < 5) {
                                Toast.makeText(getContext(), getString(R.string.battery_too_low_to_start_string), Toast.LENGTH_LONG).show();
                            } else {
                                api.setVacuumState(deviceId, newState, new Callback<Result<String>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Result<String>> call, @NonNull Response<Result<String>> response) {
                                        if(response.isSuccessful()) {
                                            Result<String> result = response.body();
                                            if(result != null) {
                                                status = "active";
                                                Toast.makeText(getContext(), getString(R.string.status_changed_string), Toast.LENGTH_LONG).show();
                                                updateButtons();
                                            } else {
                                                ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                                            }
                                        } else {
                                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Result<String>> call, @NonNull Throwable t) {
                                        ErrorHandler.handleUnexpectedError(t, requireView(), VacuumControllerFragment.this);
                                    }
                                });
                            }
                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Result<VacuumState>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t, requireView(), VacuumControllerFragment.this);
                }
            });
        } else {
            api.setVacuumState(deviceId, newState, new Callback<Result<String>>() {
                @Override
                public void onResponse(@NonNull Call<Result<String>> call, @NonNull Response<Result<String>> response) {
                    if(response.isSuccessful()) {
                        Result<String> result = response.body();
                        if(result != null) {
                            switch (newState) {
                                case "pause":
                                    status = "inactive";
                                    break;
                                case "dock":
                                    status = "docked";
                                    break;
                            }
                            Toast.makeText(getContext(), getString(R.string.status_changed_string), Toast.LENGTH_LONG).show();
                            updateButtons();
                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<String>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t, requireView(), VacuumControllerFragment.this);
                }
            });
        }
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
        }
    }

    @NonNull
    public static VacuumControllerFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);

        VacuumControllerFragment fragment = new VacuumControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void updateButtons() {
        pauseButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));
        pauseButton.setTextColor(ContextCompat.getColorStateList(requireContext(), android.R.color.black));
        pauseButton.setEnabled(true);

        startButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));
        startButton.setTextColor(ContextCompat.getColorStateList(requireContext(), android.R.color.black));
        startButton.setEnabled(true);

        dockButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));
        dockButton.setTextColor(ContextCompat.getColorStateList(requireContext(), android.R.color.black));
        dockButton.setEnabled(true);

        switch (status) {
            case "inactive":
                pauseButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.holo_blue_light));
                pauseButton.setTextColor(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
                pauseButton.setEnabled(false);
                break;
            case "docked":
                dockButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.holo_blue_light));
                dockButton.setTextColor(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
                dockButton.setEnabled(false);
                break;
            case "active":
                startButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.holo_blue_light));
                startButton.setTextColor(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
                startButton.setEnabled(false);
                break;
        }
    }

    private void startBatteryThread() {
        new Thread(() -> {
            while(runThreads) {
                api.getVacuumState(deviceId, new Callback<Result<VacuumState>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<VacuumState>> call, @NonNull Response<Result<VacuumState>> response) {
                        if(response.isSuccessful()) {
                            Result<VacuumState> result = response.body();
                            if(result != null) {
                                VacuumState vacuumState = result.getResult();
                                if(runThreads)
                                    batteryTextView.setText(getString(R.string.battery_string) + " " + vacuumState.getBatteryLevel() + "%");
                            } else {
                                ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                            }
                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result<VacuumState>> call, @NonNull Throwable t) {
                        ErrorHandler.handleUnexpectedError(t, requireView(), VacuumControllerFragment.this);
                    }
                });

                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
