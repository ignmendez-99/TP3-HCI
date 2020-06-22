package com.example.ultrahome.ui.rooms;

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
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.Room;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRoomDialog extends Dialog {

    private RoomsFragment fragmentInstance;
    private Context context;
    private Button add_button;
    private Button cancel_button;
    private EditText roomNameEditText;
    private TextView errorMessage;
    private String roomName;
    private Room newRoom;
    private String homeId;

    public AddRoomDialog(@NonNull Context context, String homeId, RoomsFragment homesFragment) {
        super(context);
        this.context = context;
        this.homeId = homeId;
        fragmentInstance = homesFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_room);

        add_button = findViewById(R.id.button_show_AddRoomDialog);
        cancel_button = findViewById(R.id.button_close_add_room_dialog);
        roomNameEditText = findViewById(R.id.room_name_edit_text);
        errorMessage = findViewById(R.id.dialog_add_room_error_message);
        add_button.setOnClickListener(this::checkCorrectInput);

        cancel_button.setOnClickListener(v -> dismiss());
    }

    private void checkCorrectInput(View v) {
        roomName = roomNameEditText.getText().toString();
        if(roomName.length() > 60 || roomName.length() < 3) {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText("Name must be between 3 and 60 characters");
        } else {
            if (!roomName.matches("^[a-zA-Z0-9_ ]{3,60}")) {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Name must only contain numbers, digits, spaces or _");
            } else {
                addNewRoom(v);
            }
        }
    }

    private void addNewRoom(View v) {
        findViewById(R.id.loadingAddingRoom).setVisibility(View.VISIBLE);
        findViewById(R.id.add_room_buttom_pair).setVisibility(View.GONE);
        newRoom = new Room(roomName);
        new Thread(() -> {
            ApiClient.getInstance().addRoom(newRoom, new Callback<Result<Room>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Room>> call, @NonNull Response<Result<Room>> response) {
                    if(response.isSuccessful()) {
                        Result<Room> result = response.body();
                        if(result != null) {
                            String temporalId = result.getResult().getId();
                            linkNewRoomWithThisHome(v, temporalId);
                        } else
                            Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                    } else
                        ErrorHandler.handleError(response, context);
                    findViewById(R.id.loadingAddingRoom).setVisibility(View.GONE);
                    findViewById(R.id.add_room_buttom_pair).setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure(@NonNull Call<Result<Room>> call, @NonNull Throwable t) {
                    findViewById(R.id.loadingAddingRoom).setVisibility(View.GONE);
                    findViewById(R.id.add_room_buttom_pair).setVisibility(View.VISIBLE);
                    ErrorHandler.handleUnexpectedError(t);
                }
            });
        }).start();
    }

    private void linkNewRoomWithThisHome(View v, String newRoomId) {
        ApiClient.getInstance().linkRoomWithHome(homeId, newRoomId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null && result.getResult()) {
                        fragmentInstance.notifyNewRoomAdded(newRoomId, roomName);
                        dismiss();
                    } else
                        Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    ErrorHandler.handleError(response, context);
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t);
                // todo: faltaria eliminar la Room ya creada, ya que hubo error al linkearla con la home
            }
        });
    }
}


