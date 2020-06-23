package com.example.ultrahome.ui.devices;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DeleteDeviceConfirmationDialog extends DialogFragment {

    private DevicesListFragment containerFragment;
    private boolean mustRecoverDevice = true;

    public DeleteDeviceConfirmationDialog(DevicesListFragment containerFragment) {
        this.containerFragment = containerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete this Device?")
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dismiss();
                })
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    mustRecoverDevice = false;
                    containerFragment = (DevicesListFragment) getParentFragment();
                    assert containerFragment != null;
                    containerFragment.deleteDevice(containerFragment.getView());
                })
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mustRecoverDevice) {
            containerFragment.recoverRemovedDevice(containerFragment.getView());
        }
    }
}
