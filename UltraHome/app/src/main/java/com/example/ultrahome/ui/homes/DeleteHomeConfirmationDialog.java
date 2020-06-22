package com.example.ultrahome.ui.homes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DeleteHomeConfirmationDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete this Home?")
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    HomesFragment containerFragment = (HomesFragment) getParentFragment();
                    assert containerFragment != null;
                    containerFragment.recoverRemovedHome();
                })
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    HomesFragment containerFragment = (HomesFragment) getParentFragment();
                    assert containerFragment != null;
                    containerFragment.deleteHome(containerFragment.getView());
                })
                .create();
    }
}
