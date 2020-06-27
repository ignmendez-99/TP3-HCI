package com.example.ultrahome.ui.rooms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DeleteRoomConfirmationDialog extends DialogFragment {

    private RoomsFragment containerFragment;
    private boolean mustRecoverRoom = true;

    public DeleteRoomConfirmationDialog(RoomsFragment containerFragment) {
        this.containerFragment = containerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // todo: hardcoded string
                .setTitle("Warning!")
                // todo: hardcoded string
                .setMessage("Are you sure you want to delete this Room?")
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dismiss();
                })
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    mustRecoverRoom = false;
                    containerFragment = (RoomsFragment) getParentFragment();
                    assert containerFragment != null;
                    containerFragment.deleteRoom(containerFragment.getView());
                })
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mustRecoverRoom) {
            containerFragment.recoverRemovedRoom(containerFragment.getView());
        }
    }
}
