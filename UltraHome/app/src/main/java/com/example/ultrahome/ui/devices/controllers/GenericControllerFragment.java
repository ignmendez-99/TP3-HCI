//package com.example.ultrahome.ui.devices.controllers;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.ultrahome.R;
//import com.example.ultrahome.apiConnection.ApiClient;
//import com.example.ultrahome.apiConnection.entities.Error;
//
//import retrofit2.Response;
//
//public abstract class GenericControllerFragment extends Fragment {
//    String deviceId;
//    int positionInRecyclerView;
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        readBundle(getArguments());
//
//        init(getView());
//    }
//
//    abstract void init(View view);
//
//    private void readBundle(Bundle bundle) {
//        if (bundle != null) {
//            deviceId = bundle.getString("deviceId");
//            positionInRecyclerView = bundle.getInt("positionInRecyclerView");
//        }
//    }
//
//    private <T> void handleError(@NonNull Response<T> response) {
//        Error error = ApiClient.getInstance().getError(response.errorBody());
//        String text = "ERROR" + error.getDescription().get(0) + error.getCode();
//        Log.e("com.example.ultrahome", text);
//        Toast.makeText(getContext(), "OOPS! AN UNEXPECTED ERROR OCURRED :(", Toast.LENGTH_LONG).show();
//    }
//
//    private void handleUnexpectedError(@NonNull Throwable t) {
//        String LOG_TAG = "com.example.ultrahome";
//        Log.e(LOG_TAG, t.toString());
//        Toast.makeText(getContext(), "OOPS! THERE'S A PROBLEM ON OUR SIDE :(", Toast.LENGTH_LONG).show();
//    }
//}
