package com.example.ultrahome.ui.devices.controllers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ultrahome.MainActivity;
import com.example.ultrahome.R;
import com.example.ultrahome.ui.devices.DevicesListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BlindsControllerFragment extends Fragment {

    private Button deleteButton;
    private FloatingActionButton editButton;
    private FloatingActionButton doneButton;
    private EditText nameEdited;
    private TextView name;

    private String currentName = "PRUEBA";

    private String deviceId;
    private int positionInRecyclerView;

    private boolean editing = false;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blinds_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readBundle(getArguments());

        deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this::deletePressed);
        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setClickable(false);

        doneButton = view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(this::editPressed);
        doneButton.setVisibility(View.INVISIBLE);
        doneButton.setClickable(false);

        editButton = view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(this::editPressed);

        nameEdited = view.findViewById(R.id.name_edited);
        nameEdited.setVisibility(View.INVISIBLE);

        name = view.findViewById(R.id.name);
        name.setText(currentName);

        nameEdited.setText(currentName);


    }

    private void editPressed(View view) {
        if (editing) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setClickable(true);
            deleteButton.setVisibility(View.INVISIBLE);
            deleteButton.setClickable(false);
            doneButton.setVisibility(View.INVISIBLE);
            doneButton.setClickable(false);
            nameEdited.setVisibility(View.INVISIBLE);
            name.setVisibility(View.VISIBLE);

            if (! nameEdited.getText().toString().equals(currentName)) {
                currentName = nameEdited.getText().toString();
                name.setText(currentName);
                // API CALL PARA RENOMBRAR
            }
        } else {
            editButton.setVisibility(View.INVISIBLE);
            editButton.setClickable(false);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setClickable(true);
            doneButton.setVisibility(View.VISIBLE);
            doneButton.setClickable(true);
            nameEdited.setVisibility(View.VISIBLE);
            name.setVisibility(View.INVISIBLE);
        }
        editing = !editing;
    }

    private void deletePressed(View view) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Create and show the dialog.
        ConfirmationDialog newFragment = new ConfirmationDialog ();
        newFragment.show(ft, "dialog");

    }

    private void deleteDevice(View view) {
        DevicesListFragment containerFragment = (DevicesListFragment) getParentFragment();
        assert containerFragment != null;
        containerFragment.deleteDevice(view, positionInRecyclerView);
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
        }
    }

    @NonNull
    public static BlindsControllerFragment newInstance(String deviceId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        BlindsControllerFragment fragment = new BlindsControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}
