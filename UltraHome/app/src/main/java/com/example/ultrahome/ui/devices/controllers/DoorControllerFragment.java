package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.util.Log;
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
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.DoorState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoorControllerFragment extends Fragment {

    private ApiClient api;

    private String deviceId;
    private int positionInRecyclerView;

    private Switch openCloseSwitch, lockUnlockSwitch;

    private boolean isLocked, isOpen;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_door_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        api = ApiClient.getInstance();

        openCloseSwitch = view.findViewById(R.id.open_switch);
        lockUnlockSwitch = view.findViewById(R.id.lock_switch);



        api.getDoorState(deviceId, new Callback<Result<DoorState>>() {
            @Override
            public void onResponse(Call<Result<DoorState>> call, Response<Result<DoorState>> response) {
                if(response.isSuccessful()) {
                    Result<DoorState> result = response.body();
                    if(result != null) {
                        DoorState doorState = result.getResult();
                        isOpen = doorState.isOpen();
                        isLocked = doorState.isLocked();
                        System.out.println("isOpen: " + isOpen);
                        System.out.println("isLocked: " + isLocked);
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
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<Result<DoorState>> call, Throwable t) {
                handleUnexpectedError(t);
            }
        });


    }

    private <T> void handleError(@NonNull Response<T> response) {
        Error error = ApiClient.getInstance().getError(response.errorBody());
        String text = "ERROR" + error.getDescription().get(0) + error.getCode();
        Log.e("com.example.ultrahome", text);
        Toast.makeText(getContext(), "OOPS! AN UNEXPECTED ERROR OCURRED :(", Toast.LENGTH_LONG).show();
    }

    private void handleUnexpectedError(@NonNull Throwable t) {
        String LOG_TAG = "com.example.ultrahome";
        Log.e(LOG_TAG, t.toString());
        Toast.makeText(getContext(), "OOPS! THERE'S A PROBLEM ON OUR SIDE :(", Toast.LENGTH_LONG).show();
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
        }
    }

    private void openDoor() {
        if(isOpen) {
            Toast.makeText(getContext(), "THE DOOR IS ALREADY OPEN", Toast.LENGTH_LONG).show();
            return;
        }
        if(isLocked) {
            Toast.makeText(getContext(), "THE DOOR IS LOCKED!", Toast.LENGTH_LONG).show();
            return;
        }

        api.openDoor(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), "OPENING DOOR", Toast.LENGTH_LONG).show();
                        isOpen = true;
                        lockUnlockSwitch.setEnabled(false);
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    private void closeDoor() {
        if(!isOpen || isLocked) {
            Toast.makeText(getContext(), "THE DOOR IS ALREADY CLOSED", Toast.LENGTH_LONG).show();
            return;
        }
        api.closeDoor(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), "CLOSING DOOR", Toast.LENGTH_LONG).show();
                        isOpen = false;
                        lockUnlockSwitch.setEnabled(true);
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    private void lockDoor() {
        if(isLocked) {
            Toast.makeText(getContext(), "THE DOOR IS ALREADY LOCKED", Toast.LENGTH_LONG).show();
            return;
        }
        if(isOpen) {
            Toast.makeText(getContext(), "THE DOOR IS OPEN!", Toast.LENGTH_LONG).show();
            return;
        }

        api.lockDoor(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), "LOCKING DOOR", Toast.LENGTH_LONG).show();
                        isLocked = true;
                        openCloseSwitch.setEnabled(false);
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    private void unlockDoor() {
        if(isOpen || !isLocked) {
            Toast.makeText(getContext(), "THE DOOR IS ALREADY UNLOCKED", Toast.LENGTH_LONG).show();
            return;
        }

        api.unlockDoor(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), "UNLOCKING DOOR", Toast.LENGTH_LONG).show();
                        isLocked = false;
                        openCloseSwitch.setEnabled(true);
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    @NonNull
    public static DoorControllerFragment newInstance(String deviceId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        DoorControllerFragment fragment = new DoorControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}

