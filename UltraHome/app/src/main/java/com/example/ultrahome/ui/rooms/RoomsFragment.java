package com.example.ultrahome.ui.rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.Room;
import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.ui.TabletFragment;
import com.example.ultrahome.ui.homes.HomeToRoomViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomsFragment extends Fragment {

    // Screen controls
    private FloatingActionButton addNewRoomButton;

    // variables for dealing with the RecyclerView
    private RecyclerView recyclerView;
    private Integer positionToDelete;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private RoomsAdapter adapter;

    private List<String> roomNames, roomIds, roomNamesBackupBeforeDeleting;
    private Map<Integer, Integer> devicesInEachRoom;

    private Snackbar deletingRoomSnackbar;
    private String homeId;   // this is the home that contains all rooms displayed in this screen
    private boolean fragmentOnScreen = true, deletingRoom = false;
    private ApiClient api;

    // tablet-specific variables
    private boolean inTablet = false;
    private Integer positionOfRoomOpened;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* will only get something if we are in Tablet mode */
        readBundle(getArguments());

        roomIds = new ArrayList<>();
        roomNames = new ArrayList<>();
        roomNamesBackupBeforeDeleting = new ArrayList<>();
        devicesInEachRoom = new HashMap<>();
        api = ApiClient.getInstance();

        // we grab the homeId that HomesFragment left us
        HomeToRoomViewModel model = new ViewModelProvider(requireActivity()).get(HomeToRoomViewModel.class);
        homeId = model.getHomeId().getValue();

        addNewRoomButton = view.findViewById(R.id.button_show_AddRoomDialog);
        addNewRoomButton.setOnClickListener(this::showAddRoomDialog);

        setupRecyclerView(view);

        // If there is a savedState, we retrieve it and we DON'T call the API.
        if(savedInstanceState != null) {
            recoverSavedState(savedInstanceState, view);
        } else {
            getAllRoomsOfThisHome(view);
        }

        // Swipe to delete functionality is assigned here
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteRoomCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /* will only get something if we are in Tablet mode */
    private void readBundle(Bundle bundle) {
        if(bundle != null) {
            inTablet = bundle.getBoolean("inTablet");
        }
    }

    private void setupRecyclerView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.rooms_recycler_view);
        if(recyclerView == null) {
            recyclerView = view.findViewById(R.id.rooms_recycler_view_grid);
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new RoomsAdapterGrid(getContext(), roomNames, this);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RoomsAdapterLinear(getContext(), roomNames, this);
        }
        recyclerView.setAdapter(adapter);
    }

    private void recoverSavedState(@NonNull Bundle savedInstanceState, View view) {
        int numberOfRoomsSaved = savedInstanceState.getInt("numberOfRooms");
        for(int i = 0; i < numberOfRoomsSaved; i++) {
            roomNames.add(savedInstanceState.getString("roomName" + i));
            roomIds.add(savedInstanceState.getString("roomId" + i));
            adapter.notifyItemInserted(i);
        }
        if(numberOfRoomsSaved == 0) {
            view.findViewById(R.id.zero_rooms).setVisibility(View.VISIBLE);
        }
        requireView().findViewById(R.id.button_show_AddRoomDialog).setVisibility(View.VISIBLE);
        requireView().findViewById(R.id.loadingRoomsList).setVisibility(View.GONE);

        RoomsViewModel model = new ViewModelProvider(requireActivity()).get(RoomsViewModel.class);
        devicesInEachRoom = model.getDevicesInEachRoom().getValue();
        if(devicesInEachRoom != null) {
            for (Integer position : devicesInEachRoom.keySet()) {
                adapter.notifyNumberOfDevicesRetrieved(position, devicesInEachRoom.get(position));
                adapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentOnScreen = false;
        RoomsViewModel model = new ViewModelProvider(requireActivity()).get(RoomsViewModel.class);
        model.storeDevicesInEachRoom(devicesInEachRoom);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);

        if(roomNames != null) {
            outState.putInt("numberOfRooms", roomNames.size());
            for (int i = 0; i < roomNames.size(); i++) {
                outState.putString("roomName" + i, roomNames.get(i));
                outState.putString("roomId" + i, roomIds.get(i));
            }
        }
    }

    /* Called by RoomsAdapter, when a Card is clicked */
    void navigateToDevicesFragment(View view, int position) {
        // we send the roomId to the DevicesFragment, so that the correct Devices are loaded
        String idOfRoomClicked = roomIds.get(position);
        RoomToDeviceViewModel model = new ViewModelProvider(requireActivity()).get(RoomToDeviceViewModel.class);
        model.storeRoomId(idOfRoomClicked);
        model.storeRoomIds(roomIds);
        model.storeRoomNames(roomNames);
        if(inTablet) {
            if(positionOfRoomOpened == null || positionOfRoomOpened != position) {
                positionOfRoomOpened = position;
                TabletFragment parentFragment = (TabletFragment) getParentFragment();
                parentFragment.initDevicesFragment();
            }
        } else {
            final NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_RoomsFragment_to_DevicesListFragment);
        }
    }

    /* Called by the AddRoomDialog, when the Room has been successfully added */
    void notifyNewRoomAdded(String roomId, String roomName) {
        roomIds.add(roomId);
        roomNames.add(roomName);
        adapter.notifyItemInserted(roomNames.size() - 1);
        requireView().findViewById(R.id.zero_rooms).setVisibility(View.GONE);
        // todo: hardcoded string
        Snackbar.make(this.requireView(), "Room Added!", Snackbar.LENGTH_SHORT).show();
    }

    private void showAddRoomDialog(View v) {
        // Create and show the dialog.
        AddRoomDialog addHomeDialog = new AddRoomDialog(requireContext(), homeId, this);
        addHomeDialog.show();
    }

    void deleteRoom(View view) {
        if(inTablet)
            ((TabletFragment)getParentFragment()).roomWasDeleted();
        // todo: hardcoded string
        deletingRoomSnackbar = Snackbar.make(view, "Room deleted!", Snackbar.LENGTH_SHORT);
        // todo: hardcoded string
        deletingRoomSnackbar.setAction("UNDO", new UndoDeleteRoomListener());
        deletingRoom = true;
        deletingRoomSnackbar.addCallback(new DeleteRoomSnackbarTimeout());
        deletingRoomSnackbar.show();
    }

    /* this method just puts the ""removed"" Room back on screen */
    void recoverRemovedRoom(View v) {
        String roomToRetrieve = roomNamesBackupBeforeDeleting.get(0);
        roomNamesBackupBeforeDeleting.remove(0);
        roomNames.add(positionToDelete, roomToRetrieve);
        adapter.notifyItemInserted(positionToDelete);
    }

    void showDeleteRoomDialog(int position) {
        positionToDelete = position;

        // remove the Home Card from screen
        String roomNameToRemove = roomNames.get(positionToDelete);
        roomNamesBackupBeforeDeleting.add(roomNameToRemove);
        roomNames.remove(positionToDelete.intValue());
        adapter.notifyItemRemoved(positionToDelete);
        adapter.notifyItemRangeChanged(positionToDelete, roomNames.size());

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        // Create and show the dialog.
        DeleteRoomConfirmationDialog newFragment = new DeleteRoomConfirmationDialog(this);
        newFragment.show(ft, "dialog");
    }

    private void getAllRoomsOfThisHome(View v) {
        int[] numberOfRoomsInThisHome = {0};
        new Thread(() -> {
            api.getRooms(new Callback<Result<List<Room>>>() {
                @Override
                public void onResponse(@NonNull Call<Result<List<Room>>> call, @NonNull Response<Result<List<Room>>> response) {
                    if (response.isSuccessful()) {
                        Result<List<Room>> result = response.body();
                        if (result != null) {
                            List<Room> roomList = result.getResult();
                            if (roomList.size() != 0) {
                                for (Room room : roomList) {
                                    if (room.getHome() == null) {
                                        // The Home containing this room was deleted! We must delete this Room
                                        deleteUselessRoom(room, v);
                                    } else {
                                        if (room.getHome().getId().equals(homeId)) {
                                            String roomId = room.getId();
                                            roomIds.add(roomId);
                                            roomNames.add(room.getName());
                                            getAmountOfDevicesInThisRoom(roomId, roomNames.size() - 1);
                                            adapter.notifyItemInserted(roomNames.size() - 1);
                                            numberOfRoomsInThisHome[0]++;
                                        }
                                    }
                                }
                                if(numberOfRoomsInThisHome[0] == 0)
                                    v.findViewById(R.id.zero_rooms).setVisibility(View.VISIBLE);
                            } else
                                v.findViewById(R.id.zero_rooms).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.button_show_AddRoomDialog).setVisibility(View.VISIBLE);
                        } else {
                            ErrorHandler.logError(response);
                            if (fragmentOnScreen)
                                showGetRoomsError();
                        }
                    } else {
                        ErrorHandler.logError(response);
                        if(fragmentOnScreen)
                            showGetRoomsError();
                    }
                    v.findViewById(R.id.loadingRoomsList).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Room>>> call, @NonNull Throwable t) {
                    if (fragmentOnScreen) {
                        showGetRoomsError();
                        ErrorHandler.handleUnexpectedError(t, requireView(), RoomsFragment.this);
                    }
                }
            });
        }).start();
    }

    /* For updating the TextView that says "3 devices inside" */
    private void getAmountOfDevicesInThisRoom(String roomId, int positionToChange) {
        new Thread(() -> {
            api.getDevicesInThisRoom(roomId, new Callback<Result<List<Device>>>() {
                @Override
                public void onResponse(@NonNull Call<Result<List<Device>>> call, @NonNull Response<Result<List<Device>>> response) {
                    if(response.isSuccessful()) {
                        Result<List<Device>> result = response.body();
                        if(result != null) {
                            List<Device> listOfDevices = result.getResult();
                            int numberOfDevices = listOfDevices.size();
                            devicesInEachRoom.put(positionToChange, numberOfDevices);
                            adapter.notifyNumberOfDevicesRetrieved(positionToChange, numberOfDevices);
                            adapter.notifyItemChanged(positionToChange);
                        } else {
                            ErrorHandler.logError(response);
                        }
                    } else {
                        ErrorHandler.logError(response);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Device>>> call, @NonNull Throwable t) {
                    if(fragmentOnScreen)
                        ErrorHandler.handleUnexpectedError(t, requireView(), RoomsFragment.this);
                }
            });
        }).start();
    }

    private void showGetRoomsError() {
        requireView().findViewById(R.id.get_rooms_failed).setVisibility(View.VISIBLE);
        requireView().findViewById(R.id.button_get_rooms_again).setOnClickListener(RoomsFragment.this::getRoomsAgain);
        requireView().findViewById(R.id.loadingRoomsList).setVisibility(View.GONE);
    }

    private void showDeleteRoomError() {
        // todo: hardcoded string
        Snackbar s = Snackbar.make(requireView(), "Could not delete Room!", Snackbar.LENGTH_SHORT);
        // todo: hardcoded string
        s.setAction("CLOSE", RoomsFragment.this::recoverRemovedRoom);
        s.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                RoomsFragment.this.recoverRemovedRoom(RoomsFragment.this.getView());
            }
        });
        s.show();
    }

    private void getRoomsAgain(View v) {
        requireView().findViewById(R.id.get_rooms_failed).setVisibility(View.GONE);
        requireView().findViewById(R.id.loadingRoomsList).setVisibility(View.VISIBLE);
        getAllRoomsOfThisHome(requireView());
    }

    /* this method deletes a Room which has no Home, therefore its useless in our App */
    private void deleteUselessRoom(@NonNull Room r, View v) {
        // There's no need for a new Thread, as this function is already called inside one!
        api.deleteRoom(r.getId(), new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(!response.isSuccessful()) {
                    ErrorHandler.logError(response);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                if(fragmentOnScreen)
                    ErrorHandler.handleUnexpectedError(t, requireView(), RoomsFragment.this);
            }
        });
    }


    /* The only thing that the UNDO action does, is closing the Snackbar and putting the
       room on screen again */
    private class UndoDeleteRoomListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String roomToRetrieve = roomNamesBackupBeforeDeleting.get(0);
            roomNamesBackupBeforeDeleting.remove(0);
            roomNames.add(positionToDelete, roomToRetrieve);
            adapter.notifyItemInserted(positionToDelete);
            deletingRoomSnackbar.dismiss();
        }
    }


    /* In the moment that the delete-room-snackbar disappears, the Room is deleted from DataBase */
    private class DeleteRoomSnackbarTimeout extends BaseTransientBottomBar.BaseCallback<Snackbar> {

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(deletingRoom) {
                deletingRoom = false;
                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                    super.onDismissed(transientBottomBar, event);
                    new Thread(() -> {
                        api.deleteRoom(roomIds.get(positionToDelete), new Callback<Result<Boolean>>() {
                            @Override
                            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                                if (response.isSuccessful()) {
                                    Result<Boolean> result = response.body();
                                    if (result != null && result.getResult()) {
                                        roomIds.remove(positionToDelete.intValue());
                                        roomNamesBackupBeforeDeleting.remove(0);
                                        if(roomIds.size() == 0 && fragmentOnScreen)
                                            RoomsFragment.this.requireView().findViewById(R.id.zero_rooms).setVisibility(View.VISIBLE);
                                    } else {
                                        ErrorHandler.logError(response);
                                        if(fragmentOnScreen)
                                            showDeleteRoomError();
                                    }
                                } else {
                                    ErrorHandler.logError(response);
                                    if(fragmentOnScreen)
                                        showDeleteRoomError();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                                if(fragmentOnScreen)
                                    showDeleteRoomError();
                            }
                        });
                    }).start();
                }
            }
        }
    }
}
