package com.example.ultrahome.ui.devices.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.ui.devices.DevicesListFragment;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefrigeratorControllerFragment extends Fragment {

    private Button buttonDeleteDevice;
    private String deviceId;
    private int positionInRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refrigerator_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readBundle(getArguments());

        buttonDeleteDevice = view.findViewById(R.id.button_delete_refrigerator);
        buttonDeleteDevice.setOnClickListener(this::deleteDevice);
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
    public static RefrigeratorControllerFragment newInstance(String deviceId, int positionInRecyclerView) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putInt("positionInRecyclerView", positionInRecyclerView);

        RefrigeratorControllerFragment fragment = new RefrigeratorControllerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void handleUnexpectedError(@NonNull Throwable t) {
        String LOG_TAG = "com.example.ultrahome";
        Log.e(LOG_TAG, t.toString());
    }

    private <T> void handleError(@NonNull Response<T> response) {
        Error error = ApiClient.getInstance().getError(response.errorBody());
        String text = "ERROR" + error.getDescription().get(0) + error.getCode();
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

}
