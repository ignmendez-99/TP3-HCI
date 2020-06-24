package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.DoorState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoorControllerFragment extends Fragment {

    private ApiClient api;

    private String deviceId;

    private Switch openCloseSwitch, lockUnlockSwitch;

    private boolean isLocked, isOpen;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_door_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        init(requireView());
    }

    private void init(View view) {
        api = ApiClient.getInstance();

        openCloseSwitch = view.findViewById(R.id.open_switch);
        lockUnlockSwitch = view.findViewById(R.id.lock_switch);

        api.getDoorState(deviceId, new Callback<Result<DoorState>>() {
            @Override
            public void onResponse(@NonNull Call<Result<DoorState>> call, @NonNull Response<Result<DoorState>> response) {
                if(response.isSuccessful()) {
                    Result<DoorState> result = response.body();
                    if(result != null) {
                        DoorState doorState = result.getResult();
                        isOpen = doorState.isOpen();
                        isLocked = doorState.isLocked();
                        if(isOpen)
                            lockUnlockSwitch.setEnabled(false);
                        if(isLocked)
                            openCloseSwitch.setEnabled(false);
                        lockUnlockSwitch.setChecked(isLocked);
                        openCloseSwitch.setChecked(!isOpen);
                        if(openCloseSwitch != null && lockUnlockSwitch != null) {
                            openCloseSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if (isChecked) {
                                    closeDoor();
                                } else {
                                    openDoor();
                                }
                            });
                            lockUnlockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if (isChecked) {
                                    lockDoor();
                                } else {
                                    unlockDoor();
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
            public void onFailure(@NonNull Call<Result<DoorState>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), DoorControllerFragment.this);
            }
        });
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
        }
    }

    @NonNull
    public static DoorControllerFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);

        DoorControllerFragment fragment = new DoorControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void openDoor() {
        api.openDoor(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), getString(R.string.opening_door_string), Toast.LENGTH_LONG).show();
                        isOpen = true;
                        lockUnlockSwitch.setEnabled(false);
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), DoorControllerFragment.this);
            }
        });
    }

    private void closeDoor() {
        api.closeDoor(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), getString(R.string.closing_door_string), Toast.LENGTH_LONG).show();
                        isOpen = false;
                        lockUnlockSwitch.setEnabled(true);
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), DoorControllerFragment.this);
            }
        });
    }

    private void lockDoor() {
        api.lockDoor(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), getString(R.string.locking_door_string), Toast.LENGTH_LONG).show();
                        isLocked = true;
                        openCloseSwitch.setEnabled(false);
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), DoorControllerFragment.this);
            }
        });
    }

    private void unlockDoor() {
        api.unlockDoor(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), getString(R.string.unlocking_door_string), Toast.LENGTH_LONG).show();
                        isLocked = false;
                        openCloseSwitch.setEnabled(true);
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), DoorControllerFragment.this);
            }
        });
    }
}

