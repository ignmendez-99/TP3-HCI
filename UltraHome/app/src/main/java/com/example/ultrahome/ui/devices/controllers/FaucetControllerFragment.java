package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

    private boolean isDispensing;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_door_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readBundle(getArguments());

        openCloseSwitch = view.findViewById(R.id.lock_switch);

//        if(openCloseSwitch != null && lockUnlockSwitch != null) {
//            openCloseSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (isChecked) {
//                    closeDoor();
//                } else {
//                    openDoor();
//                }
//            });
//            lockUnlockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (isChecked) {
//                    lockDoor();
//                } else {
//                    unlockDoor();
//                }
//            });
//        }

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
        //api abrir faucet
    }

    private void closeFaucet() {
        if(!isDispensing) {
            Toast.makeText(getContext(), "THE FAUCET IS ALREADY CLOSED", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getContext(), "CLOSING FAUCET", Toast.LENGTH_LONG).show();
        isDispensing = false;
        //api cerrar faucet
    }

//    private void dispenseAmmount() {
//        if(isLocked) {
//            Toast.makeText(getContext(), "THE DOOR IS ALREADY LOCKED", Toast.LENGTH_LONG).show();
//            return;
//        }
//        if(isOpen) {
//            Toast.makeText(getContext(), "THE DOOR IS OPEN!", Toast.LENGTH_LONG).show();
//            return;
//        }
//        Toast.makeText(getContext(), "LOCKING DOOR", Toast.LENGTH_LONG).show();
//        isLocked = true;
//        openCloseSwitch.setEnabled(false);
//        //api bloquear puerta
//    }


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

