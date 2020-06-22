package com.example.ultrahome.ui.homes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Home;
import com.example.ultrahome.apiConnection.entities.Result;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddHomeDialog extends Dialog {

    private HomesFragment fragmentInstance;
    private Context context;
    private Button add_button;
    private Button cancel_button;
    private EditText homeNameEditText;
    private TextView errorMessage;
    private String homeName;
    private Home newHome;

    public AddHomeDialog(@NonNull Context context, HomesFragment homesFragment) {
        super(context);
        this.context = context;
        fragmentInstance = homesFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_home);

        add_button = findViewById(R.id.button_add_home_2);
        cancel_button = findViewById(R.id.button_close_add_home_dialog);
        homeNameEditText = findViewById(R.id.home_name_edit_text);
        errorMessage = findViewById(R.id.dialog_add_home_error_message);
        add_button.setOnClickListener(this::addNewHome);

        cancel_button.setOnClickListener(v -> dismiss());
    }

    /* Checks for correct input and, if it is valid, we call the Api */
    private void addNewHome(View v) {
        homeName = homeNameEditText.getText().toString();
        if(homeName.length() > 60 || homeName.length() < 3) {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText("Name must be between 3 and 60 characters");
        } else {
            if( ! homeName.matches("^[a-zA-Z0-9_ ]{3,60}") ) {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Name must only contain numbers, digits, spaces or _");
            } else {
                findViewById(R.id.loadingAddingHome).setVisibility(View.VISIBLE);
                findViewById(R.id.add_home_buttom_pair).setVisibility(View.GONE);
                newHome = new Home(homeName);
                new Thread(() -> {
                    ApiClient.getInstance().addHome(newHome, new Callback<Result<Home>>() {
                        @Override
                        public void onResponse(@NonNull Call<Result<Home>> call, @NonNull Response<Result<Home>> response) {
                            if(response.isSuccessful()) {
                                Result<Home> result = response.body();
                                if(result != null) {
                                    findViewById(R.id.loadingAddingHome).setVisibility(View.GONE);
                                    findViewById(R.id.add_home_buttom_pair).setVisibility(View.VISIBLE);
                                    fragmentInstance.notifyNewHomeAdded(result.getResult().getId(), homeName);
                                    dismiss();
                                } else
                                    Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                            } else
                                ErrorHandler.handleError(response, context);
                        }
                        @Override
                        public void onFailure(@NonNull Call<Result<Home>> call, @NonNull Throwable t) {
                            ErrorHandler.handleUnexpectedError(t);
                        }
                    });
                }).start();
            }
        }
    }
}
