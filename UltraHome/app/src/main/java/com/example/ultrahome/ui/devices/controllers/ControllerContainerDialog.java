package com.example.ultrahome.ui.devices.controllers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ultrahome.R;
import com.example.ultrahome.ui.devices.DevicesListFragment;

public class ControllerContainerDialog extends DialogFragment {

    private Fragment controllerFragment;
    private TextView deviceName;
    private Button closeDialog;

    public ControllerContainerDialog(Fragment controllerFragment) {
        this.controllerFragment = controllerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.controller_container_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceName = view.findViewById(R.id.device_name_in_dialog);
//        closeDialog = view.findViewById(R.id.button_close_device_controller_dialog);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.controller_container, controllerFragment).commit();
    }
}
