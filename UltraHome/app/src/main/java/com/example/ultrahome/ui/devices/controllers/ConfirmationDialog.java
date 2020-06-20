package com.example.ultrahome.ui.devices.controllers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.ultrahome.ui.devices.DevicesListFragment;
import com.example.ultrahome.ui.devices.GenericDeviceFragment;

public class ConfirmationDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete ")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing (will close dialog)
                    }
                })
                .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GenericDeviceFragment containerFragment = (GenericDeviceFragment) getParentFragment();
                        assert containerFragment != null;
                        containerFragment.deleteDevice(containerFragment.getView());
                    }
                })
                .create();
    }
}