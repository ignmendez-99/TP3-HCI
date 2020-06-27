package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ultrahome.R;

public class ControllerContainerDialog extends DialogFragment {

    private Fragment controllerFragment;
    private TextView deviceNameTextView;
    private Button closeDialog;
    private String deviceName;

    public ControllerContainerDialog(Fragment controllerFragment, String deviceName) {
        this.controllerFragment = controllerFragment;
        this.deviceName = deviceName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.controller_container_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceNameTextView = view.findViewById(R.id.device_name_in_dialog);
        deviceNameTextView.setText(deviceName);

        closeDialog = view.findViewById(R.id.button_close_device_controller_dialog);
        closeDialog.setOnClickListener(v -> getDialog().dismiss());
//        closeDialog = view.findViewById(R.id.button_close_device_controller_dialog);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.controller_container, controllerFragment).commit();
    }
}
