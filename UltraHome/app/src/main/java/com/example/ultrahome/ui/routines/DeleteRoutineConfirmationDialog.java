package com.example.ultrahome.ui.routines;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.ultrahome.R;

public class DeleteRoutineConfirmationDialog extends DialogFragment {

    private RoutinesFragment containerFragment;
    private boolean mustRecoverRoutine = true;

    public DeleteRoutineConfirmationDialog(RoutinesFragment containerFragment) {
        this.containerFragment = containerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getContext().getString(R.string.warning_string))
                .setMessage(getContext().getString(R.string.delete_routine_confirmation_string))
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dismiss();
                })
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    mustRecoverRoutine = false;
                    containerFragment = (RoutinesFragment) getParentFragment();
                    assert containerFragment != null;
                    containerFragment.deleteRoutine(containerFragment.getView());
                })
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mustRecoverRoutine) {
            containerFragment.recoverRemovedRoutine(containerFragment.getView());
        }
    }
}
