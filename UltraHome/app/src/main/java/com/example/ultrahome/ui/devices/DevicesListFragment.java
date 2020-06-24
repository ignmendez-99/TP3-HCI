package com.example.ultrahome.ui.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
import com.example.ultrahome.apiConnection.entities.Result;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevicesListFragment extends Fragment {

    private Map<String, Integer> supportedDeviceTypeIds;

    // screen controls
    private FloatingActionButton button_add_device;
    private FloatingActionButton deleteDeviceButton;
    private FloatingActionButton editButton;
    private FloatingActionButton doneButton;
    private EditText deviceNameEdited;
    private TextView deviceNameInScreen;
    private boolean editing = false;
    private String currentDeviceName;

    // variables for dealing with the RecyclerView
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DevicesListAdapter adapter;

    private List<String> devicesNames;
    private List<String> devicesIds;
    private List<String> deviceNamesBackupBeforeDeleting;
    private List<String> deviceTypeIdsBackupBeforeDeleting;
    private List<String> deviceTypeIds;

    private String roomId;   // this is the room that contains all devices displayed in this screen
    private Snackbar deletingDeviceSnackbar;
    private boolean fragmentOnScreen = true;
    private boolean childOnScreen = false;
    private boolean deletingDevice = false;
    private ApiClient api;
    private Fragment childFragment;
    private Integer positionOfDeviceDisplayed;
    private Integer positionOfDeviceDisplayedBackup; // useful when the User UNDO the Deletion

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        devicesNames = new ArrayList<>();
        devicesIds = new ArrayList<>();
        deviceNamesBackupBeforeDeleting = new ArrayList<>();
        deviceTypeIdsBackupBeforeDeleting = new ArrayList<>();
        deviceTypeIds = new ArrayList<>();
        api = ApiClient.getInstance();
        initDeviceTypeIdMap();

        // we grab the "parameter" that RoomsFragment left us
        // TODO: try this with an Intent/Bundle
        RoomToDeviceViewModel model = new ViewModelProvider(requireActivity()).get(RoomToDeviceViewModel.class);
        roomId = model.getRoomId().getValue();

        DevicesListViewModel previousState = new ViewModelProvider(requireActivity()).get(DevicesListViewModel.class);
        childFragment = previousState.getChildFragment().getValue();

        recyclerView = view.findViewById(R.id.horizontal_devices_recycler_view);
        if(recyclerView == null) {
            recyclerView = view.findViewById(R.id.vertical_devices_recycler_view);
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        }
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DevicesListAdapter(getContext(), devicesNames, this);
        recyclerView.setAdapter(adapter);

        if(savedInstanceState != null) {
            childOnScreen = savedInstanceState.getBoolean("childOnScreen");
            if(savedInstanceState.getBoolean("mustRecoverPosition")) {
                positionOfDeviceDisplayed = savedInstanceState.getInt("positionOfDeviceDisplayed");
            }
            int numberOfDevicesSaved = savedInstanceState.getInt("numberOfDevices");
            for(int i = 0; i < numberOfDevicesSaved; i++) {
                devicesNames.add(savedInstanceState.getString("deviceName" + i));
                devicesIds.add(savedInstanceState.getString("deviceId" + i));
                deviceTypeIds.add(savedInstanceState.getString("deviceTypeId" + i));
                adapter.notifyItemInserted(i);
            }
            if(numberOfDevicesSaved == 0) {
                view.findViewById(R.id.zero_devices).setVisibility(View.VISIBLE);
            }
            requireView().findViewById(R.id.button_show_AddDeviceDialog).setVisibility(View.VISIBLE);
            requireView().findViewById(R.id.loadingDevicesList).setVisibility(View.GONE);
            editing = savedInstanceState.getBoolean("editing");
            currentDeviceName = savedInstanceState.getString("deviceInScreenName");
        } else {
            getAllDevicesOfThisRoom(view);
        }

        initScreenControllers(view);
    }

    private void initScreenControllers(@NonNull View view) {
        button_add_device = view.findViewById(R.id.button_show_AddDeviceDialog);
        button_add_device.setOnClickListener(this::showAddDeviceDialog);

        deleteDeviceButton = view.findViewById(R.id.delete_device_button);
        deleteDeviceButton.setOnClickListener(this::deletePressed);
        if(childOnScreen && editing) {
            deleteDeviceButton.setVisibility(View.VISIBLE);
            deleteDeviceButton.setClickable(true);
        } else {
            deleteDeviceButton.setVisibility(View.INVISIBLE);
            deleteDeviceButton.setClickable(false);
        }

        doneButton = view.findViewById(R.id.done_editing_device_button);
        doneButton.setOnClickListener(this::editPressed);
        if(childOnScreen && editing) {
            doneButton.setVisibility(View.VISIBLE);
            doneButton.setClickable(true);
        } else {
            doneButton.setVisibility(View.INVISIBLE);
            doneButton.setClickable(false);
        }

        editButton = view.findViewById(R.id.edit_device_button);
        editButton.setOnClickListener(this::editPressed);
        if(childOnScreen && !editing) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setClickable(true);
        } else {
            editButton.setVisibility(View.INVISIBLE);
            editButton.setClickable(false);
        }

        deviceNameEdited = view.findViewById(R.id.device_name_editText);
        if(childOnScreen) {
            if(editing)
                deviceNameEdited.setVisibility(View.VISIBLE);
            deviceNameEdited.setText(currentDeviceName);
        }
        deviceNameInScreen = view.findViewById(R.id.device_name_textView);
        if(childOnScreen) {
            if(!editing)
                deviceNameInScreen.setVisibility(View.VISIBLE);
            deviceNameInScreen.setText(currentDeviceName);
        }
    }

    private void editPressed(View view) {
        if (editing) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setClickable(true);
            deleteDeviceButton.setVisibility(View.INVISIBLE);
            deleteDeviceButton.setClickable(false);
            doneButton.setVisibility(View.INVISIBLE);
            doneButton.setClickable(false);
            deviceNameEdited.setVisibility(View.INVISIBLE);
            deviceNameInScreen.setVisibility(View.VISIBLE);

            if (! deviceNameEdited.getText().toString().equals(currentDeviceName)) {
                currentDeviceName = deviceNameEdited.getText().toString();
                deviceNameInScreen.setText(currentDeviceName);
                // todo: API CALL PARA RENOMBRAR
            }
        } else {
            editButton.setVisibility(View.INVISIBLE);
            editButton.setClickable(false);
            deleteDeviceButton.setVisibility(View.VISIBLE);
            deleteDeviceButton.setClickable(true);

            doneButton.setVisibility(View.VISIBLE);
            doneButton.setClickable(true);
            deviceNameEdited.setText(currentDeviceName);
            deviceNameEdited.setVisibility(View.VISIBLE);
            deviceNameInScreen.setVisibility(View.INVISIBLE);
        }
        editing = !editing;
    }

    private void deletePressed(View view) {
        deleteDeviceButton.setVisibility(View.GONE);
        doneButton.setVisibility(View.GONE);
        doneButton.setClickable(false);
        deviceNameEdited.setVisibility(View.GONE);
        deviceNameInScreen.setVisibility(View.GONE);
        editButton.setClickable(true);
        childOnScreen = false;

        showDeleteDeviceDialog();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentOnScreen = false;
        DevicesListViewModel model = new ViewModelProvider(requireActivity()).get(DevicesListViewModel.class);
        model.storeChildFragment(childFragment);
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
                outState.putString("deviceTypeId" + i, deviceTypeIds.get(i));
            }
            outState.putBoolean("childOnScreen", childOnScreen);
            outState.putBoolean("editing", editing);
            outState.putString("deviceInScreenName", currentDeviceName);
            if(positionOfDeviceDisplayed != null) {
                outState.putBoolean("mustRecoverPosition", true);
                outState.putInt("positionOfDeviceDisplayed", positionOfDeviceDisplayed);
            } else
                outState.putBoolean("mustRecoverPosition", false);
        }
    }

    /* Called by the AddRoomDialog, when the Room has been successfully added */
    void notifyNewDeviceAdded(String deviceId, String deviceName, String deviceTypeId) {
        devicesIds.add(deviceId);
        devicesNames.add(deviceName);
        deviceTypeIds.add(deviceTypeId);
        adapter.notifyItemInserted(devicesNames.size() - 1);

        requireView().findViewById(R.id.zero_devices).setVisibility(View.GONE);
        Snackbar.make(this.requireView(), "Device Added!", Snackbar.LENGTH_SHORT).show();
    }

    private void showAddDeviceDialog(View v) {
        // Create and show the dialog.
        AddDeviceDialog addDeviceDialog = new AddDeviceDialog(requireContext(), roomId, this);
        addDeviceDialog.show();
    }

    public void showDeleteDeviceDialog() {
        // detach the Controller Fragment from screen
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.detach(childFragment).commit();
        childFragment.onDestroy();
        childFragment.onDetach();
        childFragment = null;

        // remove the Device Card from screen
        String deviceNameToRemove = devicesNames.get(positionOfDeviceDisplayed);
        String deviceTypeIdToRemove = deviceTypeIds.get(positionOfDeviceDisplayed);
        deviceNamesBackupBeforeDeleting.add(deviceNameToRemove);
        deviceTypeIdsBackupBeforeDeleting.add(deviceTypeIdToRemove);
        devicesNames.remove(positionOfDeviceDisplayed.intValue());
        deviceTypeIds.remove(positionOfDeviceDisplayed.intValue());

        adapter.notifyItemRemoved(positionOfDeviceDisplayed);
        adapter.notifyItemRangeChanged(positionOfDeviceDisplayed, devicesNames.size());

        positionOfDeviceDisplayedBackup = positionOfDeviceDisplayed;
        positionOfDeviceDisplayed = null;

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        // Create and show the dialog.
        DeleteDeviceConfirmationDialog newFragment = new DeleteDeviceConfirmationDialog(this);
        newFragment.show(ft, "dialog");
    }

    /* this method just puts the ""removed"" Device back on screen */
    void recoverRemovedDevice(View v) {
        String deviceNameToRetrieve = deviceNamesBackupBeforeDeleting.get(0);
        String deviceTypeIdToRecover = deviceTypeIdsBackupBeforeDeleting.get(0);
        deviceNamesBackupBeforeDeleting.remove(0);
        deviceTypeIdsBackupBeforeDeleting.remove(0);
        devicesNames.add(positionOfDeviceDisplayedBackup, deviceNameToRetrieve);
        deviceTypeIds.add(positionOfDeviceDisplayedBackup, deviceTypeIdToRecover);
        adapter.notifyItemInserted(positionOfDeviceDisplayedBackup);
        adapter.notifyItemRangeChanged(positionOfDeviceDisplayedBackup, devicesNames.size());
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
                                view.findViewById(R.id.zero_devices).setVisibility(View.GONE);
                            } else
                                view.findViewById(R.id.zero_devices).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.button_show_AddDeviceDialog).setVisibility(View.VISIBLE);
                        } else {
                            ErrorHandler.logError(response);
                            if (fragmentOnScreen)
                                showGetDevicesError();
                        }
                    } else {
                        ErrorHandler.logError(response);
                        if(fragmentOnScreen)
                            showGetDevicesError();
                    }
                    view.findViewById(R.id.loadingDevicesList).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Device>>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t, requireView(), DevicesListFragment.this);
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
                    ErrorHandler.logError(response);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), DevicesListFragment.this);
            }
        });
    }

    List<String> getIdList() {
        return devicesIds;
    }

    List<String> getDeviceTypeIds() {
        return deviceTypeIds;
    }

    void insertNestedFragment(String deviceTypeId, String deviceId, String deviceName, @NonNull Integer positionInRecyclerView) {
        if(!positionInRecyclerView.equals(positionOfDeviceDisplayed)) {
            positionOfDeviceDisplayed = positionInRecyclerView;
            Integer layoutToChoose = supportedDeviceTypeIds.get(deviceTypeId);
            if (layoutToChoose != null) {
                if(childFragment != null) {
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.detach(childFragment).commit();
                    childFragment.onDestroy();
                    childFragment.onDetach();
                    childFragment = null;
                }
                switch (layoutToChoose) {
                    case R.layout.fragment_lights_controller:
                        childFragment = LightsControllerFragment.newInstance(deviceId);
                        break;
                    case R.layout.fragment_blinds_controller:
                        childFragment = BlindsControllerFragment.newInstance(deviceId);
                        break;
                    case R.layout.fragment_door_controller:
                        childFragment = DoorControllerFragment.newInstance(deviceId);
                        break;
                    case R.layout.fragment_faucet_controller:
                        childFragment = FaucetControllerFragment.newInstance(deviceId);
                        break;
                    case R.layout.fragment_refrigerator_controller:
                        childFragment = RefrigeratorControllerFragment.newInstance(deviceId);
                        break;
                    case R.layout.fragment_speaker_controller:
                        childFragment = SpeakerControllerFragment.newInstance(deviceId);
                        break;
                    case R.layout.fragment_vacuum_controller:
                        childFragment = VacuumControllerFragment.newInstance(deviceId);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + layoutToChoose);
                }
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.device_control_container, childFragment).commit();
                editButton.setVisibility(View.VISIBLE);
                editButton.setClickable(true);
                deviceNameInScreen.setVisibility(View.VISIBLE);
                currentDeviceName = deviceName;
                deviceNameInScreen.setText(currentDeviceName);
                deviceNameEdited.setText(currentDeviceName);
                childOnScreen = true;
            } else {
                Snackbar.make(requireView(), "Could not load device", Snackbar.LENGTH_SHORT);
            }
        }
    }


    /* The only thing that the UNDO action does, is closing the Snackbar and putting the
       device on screen again */
    private class UndoDeleteDeviceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String deviceNameToRetrieve = deviceNamesBackupBeforeDeleting.get(0);
            String deviceTypeIdToRetrieve = deviceTypeIdsBackupBeforeDeleting.get(0);
            deviceNamesBackupBeforeDeleting.remove(0);
            deviceTypeIdsBackupBeforeDeleting.remove(0);
            devicesNames.add(positionOfDeviceDisplayedBackup, deviceNameToRetrieve);
            deviceTypeIds.add(positionOfDeviceDisplayedBackup, deviceTypeIdToRetrieve);
            adapter.notifyItemInserted(positionOfDeviceDisplayedBackup);
            adapter.notifyItemRangeChanged(positionOfDeviceDisplayedBackup, devicesNames.size());
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
                        api.deleteDevice(devicesIds.get(positionOfDeviceDisplayedBackup), new Callback<Result<Boolean>>() {
                            @Override
                            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                                if (response.isSuccessful()) {
                                    Result<Boolean> result = response.body();
                                    if (result != null && result.getResult()) {
                                        deviceNamesBackupBeforeDeleting.remove(0);
                                        deviceTypeIdsBackupBeforeDeleting.remove(0);
                                        devicesIds.remove(positionOfDeviceDisplayedBackup.intValue());
                                        if(devicesIds.size() == 0)
                                            DevicesListFragment.this.requireView().findViewById(R.id.zero_devices).setVisibility(View.VISIBLE);
                                    } else {
                                        ErrorHandler.logError(response);
                                        if(fragmentOnScreen)
                                            showDeleteDeviceError();
                                    }
                                } else {
                                    ErrorHandler.logError(response);
                                    if(fragmentOnScreen)
                                        showDeleteDeviceError();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
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
