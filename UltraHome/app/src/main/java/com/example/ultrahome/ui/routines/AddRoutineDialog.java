package com.example.ultrahome.ui.routines;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.Routine.ActionsItem;
import com.example.ultrahome.apiConnection.entities.Routine.Routine;
import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceTypeAction;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceTypeComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRoutineDialog extends Dialog {

    private RoutinesFragment fragmentInstance;
    private Button add_button;
    private Button cancel_button;
    private Button addAction;
    private TextView errorMessage;
    private TextView routineDescription;

    /////////// routine ///////////
    private EditText routineNameEditText;
    private Routine newRoutine;
    private String routineName;
    private ActionsItem newAction;
    private List<ActionsItem> listActions;

    /////////// spinners //////////
    private Spinner spinner_devices;
    private Spinner spinner_actions;
    private LinearLayout action_layout;
    private Device selectedDevice;
    private String selectedDeviceName;
    private String selectedActionName;

    /////////// devices ///////////
    private List<String> deviceNames;
    private List<String> deviceIds;
    private List<String> allDeviceTypes;
    private List<String> deviceTypeIds;
    private List<Device> devices;
    private Map<String, List<String>> deviceTypeActions;


    public AddRoutineDialog(@NonNull Context context, RoutinesFragment routinesFragment) {
        super(context);
        fragmentInstance = routinesFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_routine);

        add_button = findViewById(R.id.button_show_AddRoutineDialog);
        cancel_button = findViewById(R.id.button_close_add_routine_dialog);
        routineNameEditText = findViewById(R.id.routine_name_edit_text);
        spinner_devices = findViewById(R.id.devices_spinner);
        spinner_actions = findViewById(R.id.actions_spinner);
        addAction = findViewById(R.id.button_add_action);
        routineDescription = findViewById(R.id.selected_action);
        action_layout = findViewById(R.id.action_selection);

        deviceNames = new ArrayList<>();
        deviceIds = new ArrayList<>();
        deviceTypeIds = new ArrayList<>();
        devices = new ArrayList<>();

        //Aca va a estar la lista de actionsItem que conforman la routine
        listActions = new ArrayList<>();

        allDeviceTypes = new ArrayList<>();
        deviceTypeActions = new HashMap<>();

        getAllDevices();
        List<String> auxList = new ArrayList<>();



        ArrayAdapter<String> adapterDevices = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, deviceNames);
        adapterDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_devices.setAdapter(adapterDevices);
        spinner_devices.setSelection(0);
        spinner_devices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeviceName = spinner_devices.getSelectedItem().toString();
                selectedDevice = devices.get(position);
                if(action_layout.getVisibility()==View.GONE){
                    action_layout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapterActions = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item , deviceTypeIds);
        adapterActions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_actions.setAdapter(adapterActions);
        spinner_actions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    ((TextView) view).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                    selectedActionName = (String) parent.getItemAtPosition(position);
                }
                else{
                    ((TextView) view).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addActionToDialog(v);
            }
        });

        cancel_button.setOnClickListener(v -> dismiss());
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return -1;
    }

    private void addActionToDialog(View v){
//        newAction = new ActionsItem(selectedDevice, selectedActionName, params);
//        listActions.add(newAction);
        CharSequence oldString = routineDescription.getText();
        CharSequence newString = oldString + "\n" + selectedDeviceName + "  --->  " + selectedActionName;
        routineDescription.setText(newString);
        if(routineDescription.getVisibility()==View.GONE){
            routineDescription.setVisibility(View.VISIBLE);
        }
    }

    private void checkCorrectInput(View v) {
        routineName = routineNameEditText.getText().toString();
        if(routineName.length() > 60 || routineName.length() < 3) {
            errorMessage.setVisibility(View.VISIBLE);
            // todo: hardcoded string
            errorMessage.setText("Name must be between 3 and 60 characters");
        } else {
            if (!routineName.matches("^[a-zA-Z0-9_ ]{3,60}")) {
                errorMessage.setVisibility(View.VISIBLE);
                // todo: hardcoded string
                errorMessage.setText("Name must only contain numbers, digits, spaces or _");
            } else {
                addNewRoutine(v);
            }
        }
    }

    private void getAllDevices() {
            ApiClient.getInstance().getDevices(new Callback<Result<List<Device>>>() {
                @Override
                public void onResponse(@NonNull Call<Result<List<Device>>> call, @NonNull Response<Result<List<Device>>> response) {
                    if (response.isSuccessful()) {
                        Result<List<Device>> result = response.body();
                        if (result != null) {
                            List<Device> deviceList = result.getResult();
                            if (deviceList.size() != 0) {
                                for (Device device : deviceList) {
                                    deviceIds.add(device.getId());
                                    deviceNames.add(device.getName());
                                    devices.add(device);
                                    String deviceTypeId = device.getType().getId();
                                    deviceTypeIds.add(deviceTypeId);
                                    if(!allDeviceTypes.contains(deviceTypeId)) {
                                        allDeviceTypes.add(deviceTypeId);
                                    }
//                                    if(!deviceTypeActions.keySet().contains(deviceTypeId)){
//                                        getActions(deviceTypeId);
//                                    }
                                }
                            }
                        } else {
                            ErrorHandler.logError(response);
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                        }
                    } else {
                        ErrorHandler.logError(response);
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Device>>> call, @NonNull Throwable t) {
                    ErrorHandler.logUnexpectedError(t);
                }
            });
    }

    private void getActions(String id, List<String> auxList){
//        List<String> actionsAux = new ArrayList<>();
        ApiClient.getInstance().getDeviceType(id, new Callback<Result<DeviceTypeComplete>>() {
            @Override
            public void onResponse(@NonNull Call<Result<DeviceTypeComplete>> call, @NonNull Response<Result<DeviceTypeComplete>> response) {
                if(response.isSuccessful()){
                    Result<DeviceTypeComplete> result = response.body();
                    if(result != null){
                        DeviceTypeComplete deviceType = result.getResult();
                        for(DeviceTypeAction action : deviceType.getActions()){
                            auxList.add(action.getName());
                        }
//                        deviceTypeActions.put(id, auxList);
                    }
                    else{
                        //Snackbar.make(getCurrentFocus(),"ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                        ErrorHandler.logError(response);
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                    }
                } else {
                    ErrorHandler.logError(response);
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<DeviceTypeComplete>> call, @NonNull Throwable t) {
                ErrorHandler.logUnexpectedError(t);
            }
        });
    }

    // todo: no hay que borrar esto?
    private void addNewRoutine(View v) {
        findViewById(R.id.loadingAddingHome).setVisibility(View.VISIBLE);
        findViewById(R.id.add_home_buttom_pair).setVisibility(View.GONE);
//        newRoutine = new Routine(routineName, listActions);
//        new Thread(() -> {
//            ApiClient.getInstance().addRoutine(newRoutine, new Callback<Result<Routine>>() {
//                @Override
//                public void onResponse(@NonNull Call<Result<Routine>> call, @NonNull Response<Result<Routine>> response) {
//                    if (response.isSuccessful()) {
//                        Result<Routine> result = response.body();
//                        if (result != null) {
//                            fragmentInstance.notifyNewRoutineAdded(result.getResult().getId(), routineName);
//                            dismiss();
//                        } else
//                            Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
//                    } else
//                        ErrorHandler.handleError(response, context);
//                    findViewById(R.id.loadingAddingHome).setVisibility(View.GONE);
//                    findViewById(R.id.add_home_buttom_pair).setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<Result<Routine>> call, @NonNull Throwable t) {
//                    findViewById(R.id.loadingAddingHome).setVisibility(View.GONE);
//                    findViewById(R.id.add_home_buttom_pair).setVisibility(View.VISIBLE);
//                    ErrorHandler.handleUnexpectedError(t);
//                }
//            });
//        }).start();
    }
}
