package com.example.ultrahome.ui.homes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.ultrahome.R;

public class DeleteHomeConfirmationDialog extends DialogFragment {

    private HomesFragment containerFragment;
    private boolean mustRecoverHome = true;

    public DeleteHomeConfirmationDialog(HomesFragment containerFragment) {
        this.containerFragment = containerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getContext().getString(R.string.warning_string))
                .setMessage(getContext().getString(R.string.delete_home_confirmation_string))
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dismiss();
                })
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    mustRecoverHome = false;
                    containerFragment = (HomesFragment) getParentFragment();
                    assert containerFragment != null;
                    containerFragment.deleteHome(containerFragment.getView());
                })
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mustRecoverHome) {
            containerFragment.recoverRemovedHome(containerFragment.getView());
        }
    }


}
