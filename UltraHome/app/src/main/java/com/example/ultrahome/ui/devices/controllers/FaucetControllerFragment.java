package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;

public class FaucetControllerFragment extends Fragment {

    private String deviceId;
    private int positionInRecyclerView;

    private Switch openCloseSwitch;
    private Button dispenseExactAmountButton, stopButton, startButton, cancelButton;
    private EditText ammount;

    private boolean isDispensing;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faucet_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        openCloseSwitch = view.findViewById(R.id.faucet_switch);
        dispenseExactAmountButton = view.findViewById(R.id.dispense_exact_amount_button);
        stopButton = view.findViewById(R.id.stop_button);
        startButton = view.findViewById(R.id.start_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        ammount = view.findViewById(R.id.ammount);

        if(isDispensing)
            dispenseExactAmountButton.setVisibility(View.INVISIBLE);

        stopButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        ammount.setVisibility(View.INVISIBLE);

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
            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
        }
    }

    private void openFaucet() {
        if(isDispensing) {
            Toast.makeText(getContext(), "THE FAUCET IS ALREADY OPEN", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getContext(), "OPENING FAUCET", Toast.LENGTH_LONG).show();
        isDispensing = true;
        dispenseExactAmountButton.setVisibility(View.INVISIBLE);
        //api abrir faucet
    }

    private void closeFaucet() {
        if(!isDispensing) {
            Toast.makeText(getContext(), "THE FAUCET IS ALREADY CLOSED", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getContext(), "CLOSING FAUCET", Toast.LENGTH_LONG).show();
        isDispensing = false;
        dispenseExactAmountButton.setVisibility(View.VISIBLE);
        //api cerrar faucet
    }

    private void dispenseExactAmountButtonPressed(View view) {
        dispenseExactAmountButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        ammount.setVisibility(View.VISIBLE);
    }

    private void cancelButtonPressed(View view) {
        dispenseExactAmountButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        ammount.setVisibility(View.INVISIBLE);
    }

    private void stopDispensing(View view) {
        
    }

    private void dispenseAmount(View view) {

    }


    @NonNull
    public static FaucetControllerFragment newInstance(String deviceId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        FaucetControllerFragment fragment = new FaucetControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}

