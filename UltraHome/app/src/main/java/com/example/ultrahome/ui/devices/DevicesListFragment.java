package com.example.ultrahome.ui.devices;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.lights.Lights;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.example.ultrahome.ui.devices.controllers.BlindsControllerFragment;
import com.example.ultrahome.ui.devices.controllers.DoorControllerFragment;
import com.example.ultrahome.ui.devices.controllers.FaucetControllerFragment;
import com.example.ultrahome.ui.devices.controllers.LightsControllerFragment;
import com.example.ultrahome.ui.devices.controllers.RefrigeratorControllerFragment;
import com.example.ultrahome.ui.devices.controllers.SpeakerControllerFragment;
import com.example.ultrahome.ui.devices.controllers.VacuumControllerFragment;
import com.example.ultrahome.ui.rooms.RoomToDeviceViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevicesListFragment extends Fragment {
    private Map<String, Integer> supportedDeviceTypeIds;

    // screen controls
    private Button button_add_lights;

    // variables for dealing with the RecyclerView
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DevicesListAdapter adapter;
    private Integer positionToDelete;

    private List<String> devicesNames;
    private List<String> devicesIds;
    private List<String> deviceNamesBackupBeforeDeleting;
    private List<String> deviceTypeIds;

    private String roomId;   // this is the room that contains all devices displayed in this screen
    private Snackbar deletingDeviceSnackbar;
    private ApiClient api;
    private Fragment childFragment;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        devicesNames = new ArrayList<>();
        devicesIds = new ArrayList<>();
        deviceNamesBackupBeforeDeleting = new ArrayList<>();
        deviceTypeIds = new ArrayList<>();
        api = ApiClient.getInstance();

        // we grab the "parameter" that RoomsFragment left us
        RoomToDeviceViewModel model = new ViewModelProvider(requireActivity()).get(RoomToDeviceViewModel.class);
        roomId = model.getRoomId().getValue();

        button_add_lights = view.findViewById(R.id.button_add_device);
        button_add_lights.setOnClickListener(this::addNewDevice);

        recyclerView = view.findViewById(R.id.horizontal_devices_recycler_view);
        if(recyclerView == null) {
            recyclerView = view.findViewById(R.id.vertical_devices_recycler_view);
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new DevicesListAdapter(getContext(), devicesNames, this);
        } else {
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new DevicesListAdapter(getContext(), devicesNames, this);
        }
        recyclerView.setAdapter(adapter);

        if(savedInstanceState != null) {
            int numberOfDevicesSaved = savedInstanceState.getInt("numberOfDevices");
            for(int i = 0; i < numberOfDevicesSaved; i++) {
                devicesNames.add(savedInstanceState.getString("deviceName" + i));
                devicesIds.add(savedInstanceState.getString("deviceId" + i));
                adapter.notifyItemInserted(i);
            }
        } else {
            getAllDevicesOfThisRoom(view);
        }

        initDeviceTypeIdMap();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);

        if(devicesNames != null) {
            outState.putInt("numberOfDevices", devicesNames.size());
            for (int i = 0; i < devicesNames.size(); i++) {
                outState.putString("deviceName" + i, devicesNames.get(i));
                outState.putString("deviceId" + i, devicesIds.get(i));
            }
        }
    }

    // todo: esto quizas se puede llevar a otra Clase, ya que sino esta clase hace demasiado
    private void initDeviceTypeIdMap() {
        supportedDeviceTypeIds = new HashMap<>();
        // SPEAKER
        supportedDeviceTypeIds.put("c89b94e8581855bc", R.layout.fragment_speaker_controller);
        // FAUCET
        supportedDeviceTypeIds.put("dbrlsh7o5sn8ur4i", R.layout.fragment_faucet_controller);
        // BLINDS
        supportedDeviceTypeIds.put("eu0v2xgprrhhg41g", R.layout.fragment_blinds_controller);
        // LIGHTS
        supportedDeviceTypeIds.put("go46xmbqeomjrsjr", R.layout.fragment_lights_controller);
        // DOOR
        supportedDeviceTypeIds.put("lsf78ly0eqrjbz91", R.layout.fragment_door_controller);
        // VACUUM
        supportedDeviceTypeIds.put("ofglvd9gqx8yfl3l", R.layout.fragment_vacuum_controller);
        // REFRIGERATOR
        supportedDeviceTypeIds.put("rnizejqr2di0okho", R.layout.fragment_refrigerator_controller);
    }

    private void addNewDevice(View view) {

        String name = "Luz de Nacho " + new Random().nextInt(10000); // TODO: HARDCODEADO -> EL USUARIO DEBE ELEGIR EL NOMBRE

        int random = new Random().nextInt(6); //todo: hardcodeado ya que se elige de manera random el tipo de dispositivo a agregar
        String[] a = {"", "", "", "", "", "", ""};
        supportedDeviceTypeIds.keySet().toArray(a);
        String randomTypeId = a[random];

        // todo: hardcodeado (es siempre una Lights) --> faltaria un switch para elegir bien el tipo de objeto a instanciar
        Device device = new Lights(name, new DeviceType(randomTypeId));
        api.addDevice(device, new Callback<Result<Device>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Device>> call, @NonNull Response<Result<Device>> response) {
                if(response.isSuccessful()) {
                    Result<Device> result = response.body();
                    if(result != null) {
                        String temporalId = result.getResult().getId();
                        linkNewDeviceWithThisRoom(view, name, temporalId, randomTypeId);
                    } else
                        Snackbar.make(view, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    ErrorHandler.handleError(response, getContext());
            }
            @Override
            public void onFailure(@NonNull Call<Result<Device>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t);
            }
        });
    }

    private void linkNewDeviceWithThisRoom(View view, String newDeviceName, String newDeviceId, String deviceTypeId) {
        api.linkDeviceWithRoom(roomId, newDeviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null && result.getResult()) {
                        devicesNames.add(newDeviceName);
                        devicesIds.add(newDeviceId);
                        deviceTypeIds.add(deviceTypeId);
                        adapter.notifyItemInserted(devicesNames.size() - 1);
                        Snackbar.make(view, "Device Added!", Snackbar.LENGTH_SHORT).show();
                    } else
                        Snackbar.make(view, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    ErrorHandler.handleError(response, getContext());
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t);
                // todo: faltaria eliminar el Device ya creado, ya que hubo error al linkearlo con la Room
            }
        });
    }

    public void showDeleteDeviceDialog(int position) {
        positionToDelete = position;

        // detach the GenericDeviceFragment from screen
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.detach(childFragment).commit();

        // remove the Device Card from screen
        String roomNameToRemove = devicesNames.get(positionToDelete);
        deviceNamesBackupBeforeDeleting.add(roomNameToRemove);
        devicesNames.remove(positionToDelete.intValue());
        adapter.notifyItemRemoved(positionToDelete);
        adapter.notifyItemRangeChanged(positionToDelete, devicesNames.size());

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        // Create and show the dialog.
        DeleteDeviceConfirmationDialog newFragment = new DeleteDeviceConfirmationDialog(this);
        newFragment.show(ft, "dialog");
    }

    /* this method just puts the ""removed"" Device back on screen */
    void recoverRemovedDevice() {
        String deviceToRetrieve = deviceNamesBackupBeforeDeleting.get(0);
        deviceNamesBackupBeforeDeleting.remove(0);
        devicesNames.add(positionToDelete, deviceToRetrieve);
        adapter.notifyItemInserted(positionToDelete);
    }

    void deleteDevice(View v) {
        deletingDeviceSnackbar = Snackbar.make(v, "Device deleted!", Snackbar.LENGTH_SHORT);
        deletingDeviceSnackbar.setAction("UNDO", new UndoDeleteDeviceListener());
        deletingDeviceSnackbar.addCallback(new DeleteDeviceSnackbarTimeout(v));
        deletingDeviceSnackbar.show();
    }

    private void getAllDevicesOfThisRoom(View view) {
        new Thread(() -> {
            api.getDevices(new Callback<Result<List<Device>>>() {
                @Override
                public void onResponse(@NonNull Call<Result<List<Device>>> call, @NonNull Response<Result<List<Device>>> response) {
                    if (response.isSuccessful()) {
                        Result<List<Device>> result = response.body();
                        if (result != null) {
                            List<Device> deviceList = result.getResult();
                            if (deviceList.size() != 0) {
                                for (Device device : deviceList) {
                                    if (device.getRoom() == null) {
                                        // The Room containing this device was deleted! We must delete this Device
                                        deleteUselessDevice(device, view);
                                    } else {
                                        if (device.getRoom().getId().equals(roomId)) {
                                            devicesIds.add(device.getId());
                                            deviceTypeIds.add(device.getType().getId());
                                            devicesNames.add(device.getName());
                                            adapter.notifyItemInserted(devicesNames.size() - 1);
                                        }
                                    }
                                }
                            }
                        } else
                            Snackbar.make(view, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                    } else
                        ErrorHandler.handleError(response, getContext());
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Device>>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t);
                }
            });
        }).start();
    }

    /* this method deletes a Device which has no Room, therefore its useless in our App */
    private void deleteUselessDevice(@NonNull Device d, View v) {
        // There's no need for a new Thread, as this function is already called inside one!
        api.deleteDevice(d.getId(), new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result == null || !result.getResult())
                        Snackbar.make(v, "No se pudo eliminar un Device sin padre", Snackbar.LENGTH_LONG).show();
                } else
                    ErrorHandler.handleError(response, getContext());
            }
            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t);
            }
        });
    }

    List<String> getIdList() {
        return devicesIds;
    }

    List<String> getDeviceTypeIds() {
        return deviceTypeIds;
    }

    void insertNestedFragment(String deviceTypeId, String deviceId, String deviceName, View v, int positionInRecyclerView) {
        childFragment = GenericDeviceFragment.newInstance(deviceId, deviceName, deviceTypeId, positionInRecyclerView);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.device_control_container, childFragment).commit();
    }


    /* The only thing that the UNDO action does, is closing the Snackbar and putting the
       device on screen again */
    private class UndoDeleteDeviceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String deviceToRetrieve = deviceNamesBackupBeforeDeleting.get(0);
            deviceNamesBackupBeforeDeleting.remove(0);
            devicesNames.add(positionToDelete, deviceToRetrieve);
            adapter.notifyItemInserted(positionToDelete);
            deletingDeviceSnackbar.dismiss();
        }
    }


    /* In the moment that the delete-room-snackbar disappears, the Room is deleted from DataBase */
    private class DeleteDeviceSnackbarTimeout extends BaseTransientBottomBar.BaseCallback<Snackbar> {
        private View view;

        DeleteDeviceSnackbarTimeout(View v) {
            view = v;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                super.onDismissed(transientBottomBar, event);
                new Thread(() -> {
                    api.deleteDevice(devicesIds.get(positionToDelete), new Callback<Result<Boolean>>() {
                        @Override
                        public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                            if (response.isSuccessful()) {
                                Result<Boolean> result = response.body();
                                if (result != null && result.getResult()) {
                                    devicesIds.remove(positionToDelete.intValue());
                                    deviceTypeIds.remove(positionToDelete.intValue());
                                    deviceNamesBackupBeforeDeleting.remove(0);
                                } else
                                    Snackbar.make(view, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                            } else
                                ErrorHandler.handleError(response, getContext());
                        }

                        @Override
                        public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                            ErrorHandler.handleUnexpectedError(t);
                        }
                    });
                }).start();
            }
        }
    }
}
