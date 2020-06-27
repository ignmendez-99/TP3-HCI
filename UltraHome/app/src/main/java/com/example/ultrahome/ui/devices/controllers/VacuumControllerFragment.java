package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.vacuum.VacuumState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VacuumControllerFragment extends Fragment implements LifecycleObserver {

    private String deviceId;

    private Button startButton, pauseButton, dockButton;
    private Switch modeSwitch;
    private Spinner roomToCleanSpinner;
    private TextView batteryTextView;

    private String status, mode, location;
    private boolean runThreads = true, firstTime = true, foreground = true;

    private List<String> roomIds;
    private List<String> roomNames;
    
    private String [] roomIdsArray, roomNamesArray;

    private int notificationsId = 002;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder lowBatteryBuilder, criticalBatteryBuilder;

    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vacuum_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomNames = new ArrayList<>();
        roomIds = new ArrayList<>();

        /* get Arguments when the Fragment is created by DevicesListFragment */
        readBundle(getArguments());

        /* get saved state */
        if(savedInstanceState != null) {
            roomIds = (List<String>) savedInstanceState.getSerializable("roomIds");
            roomNames = (List<String>) savedInstanceState.getSerializable("roomNames");
        }

        init(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        runThreads = false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("roomIds", (Serializable) roomIds);
        outState.putSerializable("roomNames", (Serializable) roomNames);
    }

    private int getRoomIndex(String room) {
        int i;
        for(i = 0; i < roomNamesArray.length; i++)
            if(roomNamesArray[i].equals(room))
                return i;
        return -1;
    }
    
    private void updateState() {
        new Thread(() -> {
            while(runThreads) {
                api.getVacuumState(deviceId, new Callback<Result<VacuumState>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<VacuumState>> call, @NonNull Response<Result<VacuumState>> response) {
                        if(response.isSuccessful()) {
                            Result<VacuumState> result = response.body();
                            if(result != null && runThreads) {
                                VacuumState vacuumState = result.getResult();
                                batteryTextView.setText(getString(R.string.battery_string) + " " + vacuumState.getBatteryLevel() + "%");
                                status = vacuumState.getStatus();
                                mode = vacuumState.getMode();
                                if(mode.equals("vacuum"))
                                    modeSwitch.setChecked(false);
                                else
                                    modeSwitch.setChecked(true);
                                location = vacuumState.getLocationName();
                                roomToCleanSpinner.setSelection(getRoomIndex(location));
                                updateButtons();

                                if(vacuumState.getBatteryLevel() <= 5) {
                                    if(foreground)
                                        Toast.makeText(getContext(), getString(R.string.battery_critical_text_string), Toast.LENGTH_SHORT).show();
                                    else
                                        notificationManager.notify(notificationsId, criticalBatteryBuilder.build());
                                } else if(vacuumState.getBatteryLevel() <= 20) {
                                    if(foreground)
                                        Toast.makeText(getContext(), getString(R.string.battery_low_text_string), Toast.LENGTH_SHORT).show();
                                    else
                                        notificationManager.notify(notificationsId, lowBatteryBuilder.build());
                                }

                                roomToCleanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                                        changeLocation(roomIdsArray[getRoomIndex(roomToCleanSpinner.getSelectedItem().toString())]);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                });

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
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        foreground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        foreground = true;
    }

    private void init(View view) {
        startButton = view.findViewById(R.id.start_button);
        pauseButton = view.findViewById(R.id.pause_button);
        dockButton = view.findViewById(R.id.dock_button);
        modeSwitch = view.findViewById(R.id.vacuumMop_switch);
        roomToCleanSpinner = view.findViewById(R.id.room_to_clean_spinner);
        batteryTextView = view.findViewById(R.id.battery_level);

        roomNamesArray = new String[roomNames.size()];
        roomNames.toArray(roomNamesArray);

        roomIdsArray = new String[roomIds.size()];
        roomIds.toArray(roomIdsArray);
        

        api = ApiClient.getInstance();
        
        updateState();

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
        roomToCleanSpinner = view.findViewById(R.id.room_to_clean_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, roomNamesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomToCleanSpinner.setAdapter(adapter);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        lowBatteryBuilder = new NotificationCompat.Builder(getContext(), getString(R.string.notification_channel_id_string))
                .setSmallIcon(R.drawable.vacuum_icon_foreground)
                .setContentTitle(getString(R.string.warning_string))
                .setContentText(getString(R.string.battery_low_text_string))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        criticalBatteryBuilder = new NotificationCompat.Builder(getContext(), getString(R.string.notification_channel_id_string))
                .setSmallIcon(R.drawable.vacuum_icon_foreground)
                .setContentTitle(getString(R.string.warning_string))
                .setContentText(getString(R.string.battery_critical_text_string))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager = NotificationManagerCompat.from(getContext());
    }

    private void changeLocation(String newLocation) {
        if(location.equals(newLocation))
            return;

        api.setVacuumLocation(deviceId, newLocation, new Callback<Result<String>>() {
            @Override
            public void onResponse(@NonNull Call<Result<String>> call, @NonNull Response<Result<String>> response) {
                if(response.isSuccessful()) {
                    Result<String> result = response.body();
                    if(result != null) {
                        if(firstTime) {
                            firstTime = false;
                            return;
                        } else {
                            Toast.makeText(getContext(), getString(R.string.changed_vacuum_location_string) + " " + roomToCleanSpinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                        }
                        location = newLocation;
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
            roomIds = (List<String>) bundle.getSerializable("roomIds");
            roomNames = (List<String>) bundle.getSerializable("roomNames");
        }
    }

    @NonNull
    public static VacuumControllerFragment newInstance(String deviceId, List<String> roomIds, List<String> roomNames) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putSerializable("roomNames", (Serializable) roomNames);
        bundle.putSerializable("roomIds", (Serializable) roomIds);

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
}