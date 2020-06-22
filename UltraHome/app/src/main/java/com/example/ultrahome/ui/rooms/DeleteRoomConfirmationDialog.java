package com.example.ultrahome.ui.rooms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DeleteRoomConfirmationDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete this Room?")
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    RoomsFragment containerFragment = (RoomsFragment) getParentFragment();
                    assert containerFragment != null;
                    containerFragment.recoverRemovedRoom();
                })
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    RoomsFragment containerFragment = (RoomsFragment) getParentFragment();
                    assert containerFragment != null;
                    containerFragment.deleteRoom(containerFragment.getView());
                })
                .create();
    }
}
