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
import com.example.ultrahome.apiConnection.entities.deviceEntities.Lights;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.example.ultrahome.ui.rooms.RoomToDeviceViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevicesListFragment extends Fragment {

    private Button button_add_lights;
    private Button button_remove_lights;
    private RecyclerView recyclerView;
    private List<String> devicesNames;
    private List<String> devicesIds;
    private List<String> deviceNamesBackupBeforeDeleting;
    private String roomId;   // this is the room that contains all devices displayed in this screen
    private Integer positionToDelete;
    private Snackbar deletingDeviceSnackbar;
    private LinearLayoutManager layoutManager;
    private DevicesListAdapter adapter;
    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        devicesNames = new ArrayList<>();
        devicesIds = new ArrayList<>();
        deviceNamesBackupBeforeDeleting = new ArrayList<>();
        api = ApiClient.getInstance();

        // we grab the "parameter" that RoomsFragment left us
        RoomToDeviceViewModel model = new ViewModelProvider(requireActivity()).get(RoomToDeviceViewModel.class);
        roomId = model.getRoomId().getValue();

        // Displays in screen all Devices -->  todo: FALTA CACHE, ya que sino puede ser mucha carga?
        getAllDevicesOfThisRoom(view);

        button_add_lights = view.findViewById(R.id.button_add_lights);
        button_add_lights.setOnClickListener(this::addNewLight);

        button_remove_lights = view.findViewById(R.id.button_delete_lights);
        button_remove_lights.setOnClickListener(this::deleteDevice);

        recyclerView = view.findViewById(R.id.horizontal_devices_recycler_view);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DevicesListAdapter(getContext(), devicesNames, this);
        recyclerView.setAdapter(adapter);

        insertNestedFragment();
    }

    // todo: hardcoded as it always add a LIGHT
    private void addNewLight(View view) {
        String name = "Luz de Nacho " + new Random().nextInt(10000); // TODO: HARDCODEADO -> EL USUARIO DEBE ELEGIR EL NOMBRE
        Lights device = new Lights(name, new DeviceType("go46xmbqeomjrsjr"));  // TODO: HARDCODEADO -> SE ELIGE EL TIPO "LAMP" SIEMPRE
        api.addDevice(device, new Callback<Result<Lights>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Lights>> call, @NonNull Response<Result<Lights>> response) {
                if(response.isSuccessful()) {
                    Result<Lights> result = response.body();
                    if(result != null) {
                        String temporalId = result.getResult().getId();
                        linkNewDeviceWithThisRoom(view, name, temporalId);
                    } else
                        Snackbar.make(view, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    handleError(response);
                    //Snackbar.make(view, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull Call<Result<Lights>> call, @NonNull Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    ////////////////////////////////////
    private <T> void handleError(Response<T> response) {
        Error error = ApiClient.getInstance().getError(response.errorBody());
        String text = "ERROR" + error.getDescription().get(0) + error.getCode();
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }
    //////////////////////////////////

    private void linkNewDeviceWithThisRoom(View view, String newDeviceName, String newDeviceId) {
        api.linkDeviceWithRoom(roomId, newDeviceId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null && result.getResult()) {
                        devicesNames.add(newDeviceName);
                        devicesIds.add(newDeviceId);
                        adapter.notifyItemInserted(devicesNames.size() - 1);
                        Snackbar.make(view, "Room Added!", Snackbar.LENGTH_SHORT).show();
                    } else
                        Snackbar.make(view, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(view, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                Snackbar.make(view, "ERROR tipo 3", Snackbar.LENGTH_LONG).show();
                handleUnexpectedError(t);
                // todo: faltaria eliminar el Device ya creado, ya que hubo error al linkearlo con la Room
            }
        });
    }

    private void deleteDevice(View v) {

        // todo: DEJO ESTO PREPARADO PARA CUANDO PUEDAS ELIMINAR EL DEVICE QUE QUIERAS
        /*String roomNameToRemove = devicesNames.get(positionToDelete);
        deviceNamesBackupBeforeDeleting.add(roomNameToRemove);
        devicesNames.remove(positionToDelete.intValue());
        adapter.notifyItemRemoved(positionToDelete);

        deletingDeviceSnackbar = Snackbar.make(v, "Room deleted!", Snackbar.LENGTH_SHORT);
        deletingDeviceSnackbar.setAction("UNDO", new UndoDeleteDeviceListener());
        deletingDeviceSnackbar.addCallback(new DeleteDeviceSnackbarTimeout(v));
        deletingDeviceSnackbar.show();*/

        if(devicesNames.size() != 0) {
            String roomNameToRemove = devicesNames.get(0);
            deviceNamesBackupBeforeDeleting.add(roomNameToRemove);
            devicesNames.remove(0);
            adapter.notifyItemRemoved(0);

            deletingDeviceSnackbar = Snackbar.make(v, "Room deleted!", Snackbar.LENGTH_SHORT);
            deletingDeviceSnackbar.setAction("UNDO", new UndoDeleteDeviceListener());
            deletingDeviceSnackbar.addCallback(new DeleteDeviceSnackbarTimeout(v));
            deletingDeviceSnackbar.show();
        }

    }

    void deleteDevice(View v, int position) {
        positionToDelete = position;
        deleteDevice(v);
    }

    private void getAllDevicesOfThisRoom(View view) {
        api.getDevices(new Callback<Result<List<Lights>>>() {
            @Override
            public void onResponse(@NonNull Call<Result<List<Lights>>> call, @NonNull Response<Result<List<Lights>>> response) {
                if(response.isSuccessful()) {
                    Result<List<Lights>> result = response.body();
                    if(result != null) {
                        List<Lights> deviceList = result.getResult();
                        if(deviceList.size() != 0) {
                            for(Lights device : deviceList) {
                                if(device.getRoom() == null) {
                                    // The Room containing this device was deleted! We must delete this Device
                                    deleteUselessDevice(device, view);
                                } else {
                                    if(device.getRoom().getId().equals(roomId)) {
                                        devicesIds.add(device.getId());
                                        devicesNames.add(device.getName());
                                        adapter.notifyItemInserted(devicesNames.size() - 1);
                                    }
                                }
                            }
                        }
                    } else
                        Snackbar.make(view, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(view, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<Result<List<Lights>>> call, @NonNull Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    /* this method deletes a Device which has no Room, therefore its useless in our App */
    private void deleteUselessDevice(Lights d, View v) {
        api.deleteDevice(d.getId(), new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result == null || !result.getResult())
                        Snackbar.make(v, "No se pudo eliminar un Device sin padre", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(v, "No se pudo eliminar un Device sin padre", Snackbar.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                Snackbar.make(v, "No se pudo eliminar un Device sin padre", Snackbar.LENGTH_LONG).show();
                handleUnexpectedError(t);
            }
        });
    }

    List<String> getIdList() {
        return devicesIds;
    }

    private void insertNestedFragment() {
        Fragment childFragment = new LightsControllerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.device_control_container, childFragment).commit();
    }

    private void handleUnexpectedError(@NonNull Throwable t) {
        String LOG_TAG = "com.example.ultrahome";
        Log.e(LOG_TAG, t.toString());
    }


    /* The only thing that the UNDO action does, is closing the Snackbar and putting the
       device on screen again */
    private class UndoDeleteDeviceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // todo: DEJO ESTO PREPARADO PARA CUANDO PUEDAS ELIMINAR EL DEVICE QUE QUIERAS
            /*String deviceToRetrieve = deviceNamesBackupBeforeDeleting.get(0);
            deviceNamesBackupBeforeDeleting.remove(0);
            devicesNames.add(positionToDelete, deviceToRetrieve);
            adapter.notifyItemInserted(positionToDelete);
            deletingDeviceSnackbar.dismiss();*/

            String deviceToRetrieve = deviceNamesBackupBeforeDeleting.get(0);
            deviceNamesBackupBeforeDeleting.remove(0);
            devicesNames.add(0, deviceToRetrieve);
            adapter.notifyItemInserted(0);
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
                // todo: DEJO ESTO PREPARADO PARA CUANDO PUEDAS ELIMINAR EL DEVICE QUE QUIERAS
                //api.deleteDevice(devicesIds.get(positionToDelete), new Callback<Result<Boolean>>() {
                api.deleteDevice(devicesIds.get(0), new Callback<Result<Boolean>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                        if (response.isSuccessful()) {
                            Result<Boolean> result = response.body();
                            if (result != null && result.getResult()) {
                                // todo: DEJO ESTO PREPARADO PARA CUANDO PUEDAS ELIMINAR EL DEVICE QUE QUIERAS
                                /*devicesIds.remove(positionToDelete.intValue());
                                deviceNamesBackupBeforeDeleting.remove(0);*/
                                devicesIds.remove(0);
                                deviceNamesBackupBeforeDeleting.remove(0);
                            } else
                                Snackbar.make(view, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                        } else
                            Snackbar.make(view, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                        Snackbar.make(view, "ERROR tipo 3", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
