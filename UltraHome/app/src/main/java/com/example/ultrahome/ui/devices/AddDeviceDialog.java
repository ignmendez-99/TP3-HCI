package com.example.ultrahome.ui.devices;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.example.ultrahome.apiConnection.entities.deviceEntities.blinds.Blinds;
import com.example.ultrahome.apiConnection.entities.deviceEntities.door.Door;
import com.example.ultrahome.apiConnection.entities.deviceEntities.faucet.Faucet;
import com.example.ultrahome.apiConnection.entities.deviceEntities.lights.Lights;
import com.example.ultrahome.apiConnection.entities.deviceEntities.refrigerator.Refrigerator;
import com.example.ultrahome.apiConnection.entities.deviceEntities.speaker.Speaker;
import com.example.ultrahome.apiConnection.entities.deviceEntities.vacuum.Vacuum;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDeviceDialog extends Dialog {

    private DevicesListFragment fragmentInstance;
    private Button add_button;
    private Button cancel_button;
    private EditText deviceNameEditText;
    private TextView inputErrorMessage;
    private TextView apiErrorMessage;
    private String deviceName;
    private String roomId;

    // RadioButtons
    private RadioButton faucetRadioButton;
    private RadioButton doorRadioButton;
    private RadioButton blindsRadioButton;
    private RadioButton vacuumRadioButton;
    private RadioButton lightsRadioButton;
    private RadioButton speakerRadioButton;
    private RadioButton refrigeratorRadioButton;
    private RadioButton[] radioButtons;
    private static final String[] deviceTypeIds = {"dbrlsh7o5sn8ur4i", "lsf78ly0eqrjbz91", "eu0v2xgprrhhg41g",
            "ofglvd9gqx8yfl3l", "go46xmbqeomjrsjr", "c89b94e8581855bc", "rnizejqr2di0okho"};


    AddDeviceDialog(@NonNull Context context, String roomId, DevicesListFragment devicesListFragment) {
        super(context);
        this.roomId = roomId;
        fragmentInstance = devicesListFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_device);

        add_button = findViewById(R.id.button_add_device);
        cancel_button = findViewById(R.id.button_close_add_device_dialog);
        deviceNameEditText = findViewById(R.id.device_name_edit_text);
        inputErrorMessage = findViewById(R.id.dialog_add_device_error_message_1);
        apiErrorMessage = findViewById(R.id.dialog_add_device_error_message_2);

        add_button.setOnClickListener(this::checkCorrectInput);
        cancel_button.setOnClickListener(v -> dismiss());

        faucetRadioButton = findViewById(R.id.faucet_radioButton);
        doorRadioButton = findViewById(R.id.door_radioButton);
        blindsRadioButton = findViewById(R.id.blinds_radioButton);
        vacuumRadioButton = findViewById(R.id.vacuum_radioButton);
        lightsRadioButton = findViewById(R.id.lights_radioButton);
        speakerRadioButton = findViewById(R.id.speaker_radioButton);
        refrigeratorRadioButton = findViewById(R.id.refrigerator_radioButton);

        radioButtons = new RadioButton[]{faucetRadioButton, doorRadioButton, blindsRadioButton,
                vacuumRadioButton, lightsRadioButton, speakerRadioButton, refrigeratorRadioButton};
    }

    private void checkCorrectInput(View v) {
        deviceName = deviceNameEditText.getText().toString();
        if(deviceName.length() > 60 || deviceName.length() < 3) {
            inputErrorMessage.setVisibility(View.VISIBLE);
            inputErrorMessage.setText("Name must be between 3 and 60 characters");
        } else {
            if (!deviceName.matches("^[a-zA-Z0-9_ ]{3,60}")) {
                inputErrorMessage.setVisibility(View.VISIBLE);
                inputErrorMessage.setText("Name must only contain numbers, digits, spaces or _");
            } else {
                checkRadioButtonSelected();
            }
        }
    }

    private void checkRadioButtonSelected() {
        Device device = null;
        String deviceCheckedTypeId = null;
        int i;
        for(i = 0; i < radioButtons.length; i++) {
            if(radioButtons[i].isChecked()) {
                deviceCheckedTypeId = deviceTypeIds[i];
                DeviceType deviceType = new DeviceType(deviceCheckedTypeId);
                switch(deviceCheckedTypeId) {
                    case "dbrlsh7o5sn8ur4i":
                        device = new Faucet(deviceName, deviceType);
                        break;
                    case "lsf78ly0eqrjbz91":
                        device = new Door(deviceName, deviceType);
                        break;
                    case "eu0v2xgprrhhg41g":
                        device = new Blinds(deviceName, deviceType);
                        break;
                    case "ofglvd9gqx8yfl3l":
                        device = new Vacuum(deviceName, deviceType);
                        break;
                    case "go46xmbqeomjrsjr":
                        device = new Lights(deviceName, deviceType);
                        break;
                    case "c89b94e8581855bc":
                        device = new Speaker(deviceName, deviceType);
                        break;
                    case "rnizejqr2di0okho":
                        device = new Refrigerator(deviceName, deviceType);
                        break;
                }
            }
        }
        if(i == radioButtons.length) {
            inputErrorMessage.setVisibility(View.VISIBLE);
            inputErrorMessage.setText("A device type must be selected");
        } else {
            addNewDevice(device, deviceCheckedTypeId);
        }
    }

    private void addNewDevice(Device device, String deviceTypeId) {
        inputErrorMessage.setVisibility(View.GONE);
        findViewById(R.id.loadingAddingDevice).setVisibility(View.VISIBLE);
        findViewById(R.id.add_device_buttom_pair).setVisibility(View.GONE);
        new Thread(() -> {
            ApiClient.getInstance().addDevice(device, new Callback<Result<Device>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Device>> call, @NonNull Response<Result<Device>> response) {
                    if(response.isSuccessful()) {
                        Result<Device> result = response.body();
                        if(result != null) {
                            String temporalId = result.getResult().getId();
                            linkNewDeviceWithThisRoom(temporalId, deviceTypeId);
                        } else {
                            addDeviceFail();
                            ErrorHandler.logError(response);
                        }
                    } else {
                        addDeviceFail();
                        ErrorHandler.logError(response);
                    }
                    findViewById(R.id.loadingAddingDevice).setVisibility(View.GONE);
                    findViewById(R.id.add_device_buttom_pair).setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure(@NonNull Call<Result<Device>> call, @NonNull Throwable t) {
                    findViewById(R.id.loadingAddingDevice).setVisibility(View.GONE);
                    findViewById(R.id.add_device_buttom_pair).setVisibility(View.VISIBLE);
                    addDeviceFail();
                    ErrorHandler.logUnexpectedError(t);
                }
            });
        }).start();
    }

    private void linkNewDeviceWithThisRoom(String deviceId, String deviceTypeId) {
        ApiClient.getInstance().linkDeviceWithRoom(roomId, deviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null && result.getResult()) {
                        fragmentInstance.notifyNewDeviceAdded(deviceId, deviceName, deviceTypeId);
                        dismiss();
                    } else {
                        ErrorHandler.logError(response);
                        addDeviceFail();
                    }
                } else {
                    ErrorHandler.logError(response);
                    addDeviceFail();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.logUnexpectedError(t);
                addDeviceFail();
            }
        });
    }

    private void addDeviceFail() {
        apiErrorMessage.setVisibility(View.VISIBLE);
        apiErrorMessage.setText("Could not add new Device!");
    }
}
