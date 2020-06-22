package com.example.ultrahome.apiConnection;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ultrahome.apiConnection.entities.Error;

import retrofit2.Response;

public class ErrorHandler {

    public static <T> void handleError(@NonNull Response<T> response, Context context) {
        Error error = ApiClient.getInstance().getError(response.errorBody());
        String text = "ERROR" + error.getDescription().get(0) + error.getCode();
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void handleUnexpectedError(@NonNull Throwable t) {
        String LOG_TAG = "com.example.ultrahome";
        Log.e(LOG_TAG, t.toString());
    }
}
