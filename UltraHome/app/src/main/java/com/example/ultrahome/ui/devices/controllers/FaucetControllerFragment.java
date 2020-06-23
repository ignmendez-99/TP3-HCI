package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.faucet.FaucetState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaucetControllerFragment extends Fragment {

    private String deviceId;

    private Switch openCloseSwitch;
    private Button dispenseExactAmountButton, stopButton, startButton, cancelButton;
    private EditText amount;
    private Spinner unitSpinner;

    private ApiClient api;

    private boolean isOpen;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faucet_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        init(requireView());
    }

    private void init(View view) {
        openCloseSwitch = view.findViewById(R.id.faucet_switch);
        dispenseExactAmountButton = view.findViewById(R.id.dispense_exact_amount_button);
        stopButton = view.findViewById(R.id.stop_button);
        startButton = view.findViewById(R.id.start_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        amount = view.findViewById(R.id.amount);
        unitSpinner = view.findViewById(R.id.unit_spinner);

        api = ApiClient.getInstance();

        api.getFaucetState(deviceId, new Callback<Result<FaucetState>>() {
            @Override
            public void onResponse(@NonNull Call<Result<FaucetState>> call, @NonNull Response<Result<FaucetState>> response) {
                if(response.isSuccessful()) {
                    Result<FaucetState> result = response.body();
                    if(result != null) {
                        FaucetState faucetState = result.getResult();
                        isOpen = faucetState.isOpen();
                        openCloseSwitch.setChecked(isOpen);
                    } else {
                        ErrorHandler.handleError(response);
                        // todo: falta mensaje amigable de error
                    }
                } else {
                    ErrorHandler.handleError(response);
                    // todo: falta mensaje amigable de error
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<FaucetState>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), FaucetControllerFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });

        if(isOpen)
            dispenseExactAmountButton.setVisibility(View.INVISIBLE);

        stopButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        amount.setVisibility(View.INVISIBLE);
        unitSpinner.setVisibility(View.INVISIBLE);

        stopButton.setOnClickListener(this::stopDispensing);
        startButton.setOnClickListener(this::dispenseAmount);
        dispenseExactAmountButton.setOnClickListener(this::dispenseExactAmountButtonPressed);
        cancelButton.setOnClickListener(this::cancelButtonPressed);


        if(openCloseSwitch != null) {
            openCloseSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    openFaucet();
                } else {
                    closeFaucet();
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
    public static FaucetControllerFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);

        FaucetControllerFragment fragment = new FaucetControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void openFaucet() {
        if(isOpen) {
            Toast.makeText(getContext(), "THE FAUCET IS ALREADY OPEN", Toast.LENGTH_LONG).show();
            return;
        }

        api.openFaucet(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), "OPENING FAUCET", Toast.LENGTH_LONG).show();
                        isOpen = true;
                        dispenseExactAmountButton.setVisibility(View.INVISIBLE);
                        stopButton.setVisibility(View.INVISIBLE);
                        startButton.setVisibility(View.INVISIBLE);
                        cancelButton.setVisibility(View.INVISIBLE);
                        amount.setVisibility(View.INVISIBLE);
                        unitSpinner.setVisibility(View.INVISIBLE);
                    } else {
                        ErrorHandler.handleError(response);
                        // todo: falta mensaje amigable de error
                    }
                } else {
                    ErrorHandler.handleError(response);
                    // todo: falta mensaje amigable de error
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), FaucetControllerFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });
    }

    private void closeFaucet() {
        if(!isOpen) {
            Toast.makeText(getContext(), "THE FAUCET IS ALREADY CLOSED", Toast.LENGTH_LONG).show();
            return;
        }

        api.closeFaucet(deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), "CLOSING FAUCET", Toast.LENGTH_LONG).show();
                        isOpen = false;
                        dispenseExactAmountButton.setVisibility(View.VISIBLE);
                        stopButton.setVisibility(View.INVISIBLE);
                        startButton.setVisibility(View.INVISIBLE);
                        cancelButton.setVisibility(View.INVISIBLE);
                        amount.setVisibility(View.INVISIBLE);
                        unitSpinner.setVisibility(View.INVISIBLE);
                    } else {
                        ErrorHandler.handleError(response);
                        // todo: falta mensaje amigable de error
                    }
                } else {
                    ErrorHandler.handleError(response);
                    // todo: falta mensaje amigable de error
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), FaucetControllerFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });
    }

    private void dispenseExactAmountButtonPressed(View view) {
        dispenseExactAmountButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        amount.setVisibility(View.VISIBLE);
        unitSpinner.setVisibility(View.VISIBLE);
    }

    private void cancelButtonPressed(View view) {
        dispenseExactAmountButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        amount.setVisibility(View.INVISIBLE);
        unitSpinner.setVisibility(View.INVISIBLE);
    }

    private void stopDispensing(View view) {
        closeFaucet();
    }

    private void dispenseAmount(View view) {    // todo: VER QUE CARAJO PASA
        try{
            Integer.parseInt(amount.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "YOU MUST ENTER AN AMOUNT TO DISPENSE!", Toast.LENGTH_LONG).show();
            amount.setText("");
            return;
        }

        int amountToDispense = Integer.parseInt(amount.getText().toString());
        String unit = unitSpinner.getSelectedItem().toString();

        if(amountToDispense == 0) {
            Toast.makeText(getContext(), "YOU CAN'T DISPENSE NOTHING!", Toast.LENGTH_LONG).show();
            amount.setText("");
            return;
        }


        api.dispenseExactAmount(deviceId, amountToDispense, unit, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null) {
                        Toast.makeText(getContext(), "DISPENSING " + amountToDispense + unit, Toast.LENGTH_LONG).show();
                        openCloseSwitch.setChecked(true);
                    } else {
                        ErrorHandler.handleError(response);
                        // todo: falta mensaje amigable de error
                    }
                } else {
                    ErrorHandler.handleError(response);
                    // todo: falta mensaje amigable de error
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), FaucetControllerFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });
    }
}

