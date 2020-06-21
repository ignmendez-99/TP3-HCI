package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
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

public class BlindsControllerFragment extends Fragment {

    private String deviceId;
    private int positionInRecyclerView;

    private Button openButton, closeButton;
    private SeekBar percentageSeekBar;

    private int currentPercentage;

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

        currentPercentage = 0;  // ACA DEBERIA OBTENERLO DE LA API

        percentageSeekBar.setMax(100);
        percentageSeekBar.setProgress(currentPercentage);
        if(currentPercentage == 0)
            closeButton.setEnabled(false);
        else if(currentPercentage == 100)
            openButton.setEnabled(false);

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
//                Toast.makeText(getContext(),"seekbar touch stopped!" + aux, Toast.LENGTH_SHORT).show();
                setPercentage(percentageSeekBar.getProgress());
            }
        });
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
        }
    }

    private void openBlinds(View view) {
        if(currentPercentage == 100) {
            Toast.makeText(getContext(),"ALREADY FULLY OPEN!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(),"OPENING COMPLETELY", Toast.LENGTH_SHORT).show();
        currentPercentage = 100;
        percentageSeekBar.setProgress(100);
        closeButton.setEnabled(true);
        openButton.setEnabled(false);
        // API CALL
    }

    private void closeBlinds(View view) {
        if(currentPercentage == 0) {
            Toast.makeText(getContext(),"ALREADY FULLY CLOSED!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(),"CLOSING COMPLETELY", Toast.LENGTH_SHORT).show();
        currentPercentage = 0;
        percentageSeekBar.setProgress(0);
        closeButton.setEnabled(false);
        openButton.setEnabled(true);
        // API CALL
    }

    private void setPercentage(int newPercentage) {
        if((currentPercentage == 0 && newPercentage == 0) || (currentPercentage == 100 && newPercentage == 100))
            return;
        if(newPercentage == 100)
            openBlinds(getView());
        else if(newPercentage == 0)
            closeBlinds(getView());
        else {
            Toast.makeText(getContext(),"OPENING AT PERCENTAGE " + newPercentage, Toast.LENGTH_SHORT).show();
            currentPercentage = newPercentage;
            percentageSeekBar.setProgress(newPercentage);
            closeButton.setEnabled(true);
            openButton.setEnabled(true);
            // API CALL PARA PONER EL PORCENTAJE DE APERTURA
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

