package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.blinds.BlindsState;
import com.example.ultrahome.apiConnection.entities.deviceEntities.refrigerator.RefrigeratorState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefrigeratorControllerFragment extends Fragment {

    private String deviceId;
    private int positionInRecyclerView;

    private Spinner modeSpinner, fridgeTempSpinner, freezerTempSpinner;

    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refrigerator_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        modeSpinner = view.findViewById(R.id.mode_spinner);
        fridgeTempSpinner = view.findViewById(R.id.fridge_temp_spinner);
        freezerTempSpinner = view.findViewById(R.id.freezer_temp_spinner);

        api = ApiClient.getInstance();

        api.getRefrigeratorState(deviceId, new Callback<Result<RefrigeratorState>>() {
            @Override
            public void onResponse(Call<Result<RefrigeratorState>> call, Response<Result<RefrigeratorState>> response) {
                if(response.isSuccessful()) {
                    Result<RefrigeratorState> result = response.body();
                    if(result != null) {
                        RefrigeratorState refrigeratorState = result.getResult();
                        Integer freezerTemp = refrigeratorState.getFreezerTemperature();
                        int freezerTempIndex = getIndex(freezerTempSpinner, freezerTemp.toString());
                        Integer fridgeTemp = refrigeratorState.getTemperature();
                        int fridgeTempIndex = getIndex(fridgeTempSpinner, fridgeTemp.toString());
                        freezerTempSpinner.setSelection(freezerTempIndex);
                        fridgeTempSpinner.setSelection(fridgeTempIndex);


                        String aux = refrigeratorState.getMode();
                        switch (aux) {
                            case "default":
                                modeSpinner.setSelection(0);
                                break;
                            case "vacation":
                                modeSpinner.setSelection(1);
                                break;
                            case "party":
                                modeSpinner.setSelection(2);
                                break;
                        }

                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<Result<RefrigeratorState>> call, Throwable t) {
                handleUnexpectedError(t);
            }
        });

        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                changeMode();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }
        });

        fridgeTempSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                changeFridgeTemp();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }
        });

        freezerTempSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                changeFreezerTemp();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }
        });
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
        }
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

    private void changeMode() {
        api.changeRefrigeratorMode(deviceId, modeSpinner.getSelectedItem().toString().toLowerCase(), new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        updateSpinners();
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

    private void changeFridgeTemp() {
        api.setFridgeTemp(deviceId, Integer.parseInt(fridgeTempSpinner.getSelectedItem().toString()), new Callback<Result<Integer>>() {
            @Override
            public void onResponse(Call<Result<Integer>> call, Response<Result<Integer>> response) {
                if(response.isSuccessful()) {
                    Result<Integer> result = response.body();
                    if(result != null) {
                        updateSpinners();
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

    private void changeFreezerTemp() {
        api.setFreezerTemp(deviceId, Integer.parseInt(freezerTempSpinner.getSelectedItem().toString()), new Callback<Result<Integer>>() {
            @Override
            public void onResponse(Call<Result<Integer>> call, Response<Result<Integer>> response) {
                if(response.isSuccessful()) {
                    Result<Integer> result = response.body();
                    if(result != null) {
                        updateSpinners();
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

    private int getIndex(Spinner spinner, String myString){
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return -1;
    }

    private void updateSpinners() {
        api.getRefrigeratorState(deviceId, new Callback<Result<RefrigeratorState>>() {
            @Override
            public void onResponse(Call<Result<RefrigeratorState>> call, Response<Result<RefrigeratorState>> response) {
                if(response.isSuccessful()) {
                    Result<RefrigeratorState> result = response.body();
                    if(result != null) {
                        RefrigeratorState refrigeratorState = result.getResult();
                        Integer freezerTemp = refrigeratorState.getFreezerTemperature();
                        int freezerTempIndex = getIndex(freezerTempSpinner, freezerTemp.toString());
                        Integer fridgeTemp = refrigeratorState.getTemperature();
                        int fridgeTempIndex = getIndex(fridgeTempSpinner, fridgeTemp.toString());
                        String aux = refrigeratorState.getMode();
                        freezerTempSpinner.setSelection(freezerTempIndex);
                        fridgeTempSpinner.setSelection(fridgeTempIndex);
                        switch (aux) {
                            case "default":
                                modeSpinner.setSelection(0);
                                break;
                            case "vacation":
                                modeSpinner.setSelection(1);
                                break;
                            case "party":
                                modeSpinner.setSelection(2);
                                break;
                        }

                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<Result<RefrigeratorState>> call, Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    @NonNull
    public static RefrigeratorControllerFragment newInstance(String deviceId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        RefrigeratorControllerFragment fragment = new RefrigeratorControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}
