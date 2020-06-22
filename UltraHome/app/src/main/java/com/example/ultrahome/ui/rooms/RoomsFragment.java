package com.example.ultrahome.ui.rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.example.ultrahome.ui.homes.HomeToRoomViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> roomNames;
    private List<String> roomIds;
    private List<String> roomNamesBackupBeforeDeleting;

    private Snackbar deletingRoomSnackbar;
    private String homeId;   // this is the home that contains all rooms displayed in this screen
    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomIds = new ArrayList<>();
        roomNames = new ArrayList<>();
        roomNamesBackupBeforeDeleting = new ArrayList<>();
        api = ApiClient.getInstance();

        // we grab the homeId that HomesFragment left us
        HomeToRoomViewModel model = new ViewModelProvider(requireActivity()).get(HomeToRoomViewModel.class);
        homeId = model.getHomeId().getValue();

        addNewRoomButton = view.findViewById(R.id.button_show_AddRoomDialog);
        addNewRoomButton.setOnClickListener(this::showAddRoomDialog);

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

        // If there is a savedState, we retrieve it and we DON'T call the API.
        if(savedInstanceState != null) {
            int numberOfRoomsSaved = savedInstanceState.getInt("numberOfRooms");
            for(int i = 0; i < numberOfRoomsSaved; i++) {
                roomNames.add(savedInstanceState.getString("roomName" + i));
                roomIds.add(savedInstanceState.getString("roomId" + i));
                adapter.notifyItemInserted(i);
            }
        } else {
            getAllRoomsOfThisHome(view);
        }

        // Swipe to delete functionality is assigned here
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteRoomCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
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

    void navigateToDevicesFragment(View view, int position) {
        // we send the roomId to the DevicesFragment, so that the correct Devices are loaded
        String idOfRoomClicked = roomIds.get(position);
        RoomToDeviceViewModel model = new ViewModelProvider(requireActivity()).get(RoomToDeviceViewModel.class);
        model.storeRoomId(idOfRoomClicked);
        final NavController navController =  Navigation.findNavController(view);
        navController.navigate(R.id.action_RoomsFragment_to_DevicesListFragment);
    }

    /* Called by the AddRoomDialog, when the Room has been successfully added */
    void notifyNewRoomAdded(String roomId, String roomName) {
        roomIds.add(roomId);
        roomNames.add(roomName);
        adapter.notifyItemInserted(roomNames.size() - 1);
        Snackbar.make(this.requireView(), "Room Added!", Snackbar.LENGTH_SHORT).show();
    }

    private void showAddRoomDialog(View v) {
        // Create and show the dialog.
        AddRoomDialog addHomeDialog = new AddRoomDialog(requireContext(), homeId, this);
        addHomeDialog.show();
    }

    private void deleteRoom(View view) {
        // remove the Room Card from screen
        String roomNameToRemove = roomNames.get(positionToDelete);
        roomNamesBackupBeforeDeleting.add(roomNameToRemove);
        roomNames.remove(positionToDelete.intValue());
        adapter.notifyItemRemoved(positionToDelete);

        deletingRoomSnackbar = Snackbar.make(view, "Room deleted!", Snackbar.LENGTH_SHORT);
        deletingRoomSnackbar.setAction("UNDO", new UndoDeleteRoomListener());
        deletingRoomSnackbar.addCallback(new DeleteRoomSnackbarTimeout(view));
        deletingRoomSnackbar.show();
    }

    void deleteRoom(View v, int position) {
        positionToDelete = position;
        deleteRoom(v);
    }

    private void getAllRoomsOfThisHome(View v) {
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
                                            roomIds.add(room.getId());
                                            roomNames.add(room.getName());
                                            adapter.notifyItemInserted(roomNames.size() - 1);
                                        }
                                    }
                                }
                            }
                        } else
                            Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                    } else
                        ErrorHandler.handleError(response, getContext());
                    v.findViewById(R.id.loadingRoomsList).setVisibility(View.GONE);
                    v.findViewById(R.id.button_show_AddRoomDialog).setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Room>>> call, @NonNull Throwable t) {
                    v.findViewById(R.id.loadingRoomsList).setVisibility(View.GONE);
                    v.findViewById(R.id.button_show_AddRoomDialog).setVisibility(View.VISIBLE);
                    ErrorHandler.handleUnexpectedError(t);
                }
            });
        }).start();
    }

    /* this method deletes a Room which has no Home, therefore its useless in our App */
    private void deleteUselessRoom(@NonNull Room r, View v) {
        api.deleteRoom(r.getId(), new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result == null || !result.getResult())
                        Snackbar.make(v, "No se pudo eliminar una Room sin padre", Snackbar.LENGTH_LONG).show();
                } else
                    ErrorHandler.handleError(response, getContext());
            }
            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t);
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
        private View view;

        DeleteRoomSnackbarTimeout(View v) {
            view = v;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                super.onDismissed(transientBottomBar, event);
                api.deleteRoom(roomIds.get(positionToDelete), new Callback<Result<Boolean>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                        if (response.isSuccessful()) {
                            Result<Boolean> result = response.body();
                            if (result != null && result.getResult()) {
                                roomIds.remove(positionToDelete.intValue());
                                roomNamesBackupBeforeDeleting.remove(0);
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
            }
        }
    }
}
