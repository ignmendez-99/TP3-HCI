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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton button_add_device;

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
    private boolean fragmentOnScreen = true;
    private boolean deletingDevice = false;
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

        button_add_device = view.findViewById(R.id.button_show_AddDeviceDialog);
        button_add_device.setOnClickListener(this::showAddRoomDialog);

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentOnScreen = false;
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

    /* Called by the AddRoomDialog, when the Room has been successfully added */
    void notifyNewDeviceAdded(String deviceId, String deviceName, String deviceTypeId) {
        devicesIds.add(deviceId);
        devicesNames.add(deviceName);
        deviceTypeIds.add(deviceTypeId);
        adapter.notifyItemInserted(devicesNames.size() - 1);
        Snackbar.make(this.requireView(), "Device Added!", Snackbar.LENGTH_SHORT).show();
    }

    private void showAddRoomDialog(View v) {
        // Create and show the dialog.
        AddDeviceDialog addHomeDialog = new AddDeviceDialog(requireContext(), roomId, this);
        addHomeDialog.show();
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
    void recoverRemovedDevice(View v) {
        String deviceToRetrieve = deviceNamesBackupBeforeDeleting.get(0);
        deviceNamesBackupBeforeDeleting.remove(0);
        devicesNames.add(positionToDelete, deviceToRetrieve);
        adapter.notifyItemInserted(positionToDelete);
    }

    void deleteDevice(View v) {
        deletingDeviceSnackbar = Snackbar.make(v, "Device deleted!", Snackbar.LENGTH_SHORT);
        deletingDeviceSnackbar.setAction("UNDO", new UndoDeleteDeviceListener());
        deletingDevice = true;
        deletingDeviceSnackbar.addCallback(new DeleteDeviceSnackbarTimeout());
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
                            view.findViewById(R.id.button_show_AddDeviceDialog).setVisibility(View.VISIBLE);
                        } else
                            if(fragmentOnScreen)
                                showGetDevicesError();
                    } else {
                        ErrorHandler.handleError(response, getContext());
                        if(fragmentOnScreen)
                            showGetDevicesError();
                    }
                    view.findViewById(R.id.loadingDevicesList).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Device>>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t);
                    if(fragmentOnScreen)
                        showGetDevicesError();
                }
            });
        }).start();
    }

    private void showGetDevicesError() {
        requireView().findViewById(R.id.get_devices_failed).setVisibility(View.VISIBLE);
        requireView().findViewById(R.id.button_get_devices_again).setOnClickListener(DevicesListFragment.this::getDeviceAgain);
        requireView().findViewById(R.id.loadingDevicesList).setVisibility(View.GONE);
    }

    private void showDeleteDeviceError() {
        Snackbar s = Snackbar.make(requireView(), "Could not delete Device!", Snackbar.LENGTH_SHORT);
        s.setAction("CLOSE", DevicesListFragment.this::recoverRemovedDevice);
        s.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                DevicesListFragment.this.recoverRemovedDevice(DevicesListFragment.this.getView());
            }
        });
        s.show();
    }

    private void getDeviceAgain(View v) {
        requireView().findViewById(R.id.get_devices_failed).setVisibility(View.GONE);
        requireView().findViewById(R.id.loadingDevicesList).setVisibility(View.VISIBLE);
        getAllDevicesOfThisRoom(requireView());
    }

    /* this method deletes a Device which has no Room, therefore its useless in our App */
    private void deleteUselessDevice(@NonNull Device d, View v) {
        // There's no need for a new Thread, as this function is already called inside one!
        api.deleteDevice(d.getId(), new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(!response.isSuccessful()) {
                    ErrorHandler.handleError(response, getContext());
                }
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

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(deletingDevice) {
                deletingDevice = false;
                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
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
                                    } else {
                                        if(fragmentOnScreen)
                                            showDeleteDeviceError();
                                    }

                                } else {
                                    ErrorHandler.handleError(response, getContext());
                                    if(fragmentOnScreen)
                                        showDeleteDeviceError();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                                ErrorHandler.handleUnexpectedError(t);
                                if(fragmentOnScreen)
                                    showDeleteDeviceError();
                            }
                        });
                    }).start();
                }
            }
        }
    }
}
