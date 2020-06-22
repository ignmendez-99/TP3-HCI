package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.blinds.BlindsState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.DoorState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlindsControllerFragment extends Fragment {

    private String deviceId;
    private int positionInRecyclerView;

    private Button openButton, closeButton;
    private SeekBar percentageSeekBar;

    private int currentPercentage;

    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blinds_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        openButton = view.findViewById(R.id.open_blinds_button);
        closeButton = view.findViewById(R.id.close_blinds_button);
        percentageSeekBar = view.findViewById(R.id.percentage_blinds_seekBar);

        openButton.setOnClickListener(this::openBlinds);
        closeButton.setOnClickListener(this::closeBlinds);

        api = ApiClient.getInstance();

        api.getBlindsState(deviceId, new Callback<Result<BlindsState>>() {
            @Override
            public void onResponse(Call<Result<BlindsState>> call, Response<Result<BlindsState>> response) {
                if(response.isSuccessful()) {
                    Result<BlindsState> result = response.body();
                    if(result != null) {
                        BlindsState blindsState = result.getResult();
                        currentPercentage = blindsState.getCurrentLevel();
                        percentageSeekBar.setMax(100);
                        percentageSeekBar.setProgress(100 - currentPercentage);
                        if(currentPercentage == 100)
                            closeButton.setEnabled(false);
                        else if(currentPercentage == 0)
                            openButton.setEnabled(false);
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<Result<BlindsState>> call, Throwable t) {
                handleUnexpectedError(t);
            }
        });


        percentageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // ACA PODRIA LLAMAR A LA API EN TIEMPO REAL PERO ES UNA LOCURA
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setPercentage(percentageSeekBar.getProgress());
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

    private void openBlinds(View view) {
        if(currentPercentage == 0) {
            Toast.makeText(getContext(),"ALREADY FULLY OPEN!", Toast.LENGTH_SHORT).show();
            return;
        }

        api.openBlinds(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(),"OPENING COMPLETELY", Toast.LENGTH_SHORT).show();
                        currentPercentage = 0;
                        percentageSeekBar.setProgress(100);
                        closeButton.setEnabled(true);
                        openButton.setEnabled(false);
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

    private void closeBlinds(View view) {
        if(currentPercentage == 100) {
            Toast.makeText(getContext(),"ALREADY FULLY CLOSED!", Toast.LENGTH_SHORT).show();
            return;
        }

        api.closeBlinds(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(),"CLOSING COMPLETELY", Toast.LENGTH_SHORT).show();
                        currentPercentage = 100;
                        percentageSeekBar.setProgress(0);
                        closeButton.setEnabled(false);
                        openButton.setEnabled(true);
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

    private void setPercentage(int newPercentage) {         // todo: DOESN'T WORK
        if((currentPercentage == 100 && newPercentage == 0) || (currentPercentage == 0 && newPercentage == 100))
            return;
        if(newPercentage == 100)
            openBlinds(getView());
        else if(newPercentage == 0)
            closeBlinds(getView());
        else {
            api.setBlindsLevel(deviceId, newPercentage, new Callback<Result<Integer>>() {
                @Override
                public void onResponse(Call<Result<Integer>> call, Response<Result<Integer>> response) {
                    if(response.isSuccessful()) {
                        Result<Integer> result = response.body();
                        if(result != null) {
                            Toast.makeText(getContext(),"OPENING AT PERCENTAGE " + newPercentage, Toast.LENGTH_SHORT).show();
                            currentPercentage = newPercentage;
                            percentageSeekBar.setProgress(newPercentage);
                            closeButton.setEnabled(true);
                            openButton.setEnabled(true);
                        }
                    } else {
                        handleError(response);
                    }
                }

                @Override
                public void onFailure(Call<Result<Integer>> call, Throwable t) {
                    handleUnexpectedError(t);
                }
            });
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

