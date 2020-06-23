package com.example.ultrahome.apiConnection;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.entities.Error;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Response;

public class ErrorHandler extends Fragment{

    private static final String LOG_TAG = "com.example.ultrahome";

    public static <T> void handleError(@NonNull Response<T> response) {
        Error error = ApiClient.getInstance().getError(response.errorBody());
        String text = "ERROR" + error.getDescription().get(0) + error.getCode();
        Log.e(LOG_TAG, text);
    }

    public static void handleUnexpectedError(@NonNull Throwable t, View view, @NonNull Fragment currentFragment) {
        Log.e(LOG_TAG, t.toString());
        Snackbar snackbar = Snackbar.make(view, currentFragment.getResources().getString(R.string.handle_unexpected_error),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(currentFragment.getResources().getString(R.string.close_string), v -> {});
        snackbar.show();
    }

    public static void handleUnexpectedErrorInDialog(@NonNull Throwable t) {
        Log.e(LOG_TAG, t.toString());
    }
}
