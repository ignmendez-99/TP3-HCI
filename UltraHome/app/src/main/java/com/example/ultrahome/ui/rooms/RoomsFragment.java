package com.example.ultrahome.ui.rooms;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.Result;
import com.example.ultrahome.apiConnection.Room;
import com.example.ultrahome.ui.homes.HomeToRoomViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomsFragment extends Fragment {

    private Button addNewRoomButton;
    private Button removeRoomButton;
    private RecyclerView recyclerView;
    private String homeId;   // this is the home that contains all rooms displayed in this screen
    private List<String> roomNames;
    private List<String> roomIds;
    private List<String> roomNamesBackupBeforeDeleting;
    private Snackbar deletingRoomSnackbar;
    private Integer positionToDelete;
    private LinearLayoutManager layoutManager;
    private RoomsAdapter adapter;
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

        // we grab the "parameter" that DevicesFragment left us
        HomeToRoomViewModel model = new ViewModelProvider(requireActivity()).get(HomeToRoomViewModel.class);
        homeId = model.getHomeId().getValue();

        // Displays in screen all Rooms -->  todo: FALTA CACHE, ya que sino puede ser mucha carga?
        getAllRoomsOfThisHome(view);

        addNewRoomButton = view.findViewById(R.id.button_add_room);
        addNewRoomButton.setOnClickListener(this::addNewRoom);

        recyclerView = view.findViewById(R.id.rooms_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RoomsAdapter(getContext(), roomNames, this);
        recyclerView.setAdapter(adapter);

        // Swipe to delete functionality is assigned here
        // recyclerView.setAdapter(adapter);
        // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteRoomCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    List<String> getIdList() {
        return roomIds;
    }

    void navigateToDevicesFragment(View view) {
        final NavController navController =  Navigation.findNavController(view);
        navController.navigate(R.id.devicesFragment);
    }

    private void addNewRoom(View v) {
        String name = "Cocina de Nacho 2 " + new Random().nextInt(10000); // TODO: HARDCODEADO -> EL USUARIO DEBE ELEGIR EL NOMBRE
        Room newRoom = new Room(name);
        api.addRoom(newRoom, new Callback<Result<Room>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Room>> call, @NonNull Response<Result<Room>> response) {
                if(response.isSuccessful()) {
                    Result<Room> result = response.body();
                    if(result != null) {
                        String temporalId = result.getResult().getId();
                        linkNewRoomWithThisHome(v, name, temporalId);
                    } else
                        Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(v, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull Call<Result<Room>> call, @NonNull Throwable t) {
                Snackbar.make(v, "ERROR tipo 3", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void linkNewRoomWithThisHome(View v, String newRoomName, String newRoomId) {
        api.linkRoomWithHome(homeId, newRoomId, new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result != null && result.getResult()) {
                        roomIds.add(newRoomId);
                        roomNames.add(newRoomName);
                        adapter.notifyItemInserted(roomNames.size() - 1);
                        Snackbar.make(v, "Room Added!", Snackbar.LENGTH_SHORT).show();
                    } else
                        Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(v, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                handleUnexpectedError(t);
                // todo: faltaria eliminar la Room ya creada, ya que hubo error al linkearla con la home
            }
        });
    }

    private void deleteRoom(View view) {
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
        api.getRooms(new Callback<Result<List<Room>>>() {
            @Override
            public void onResponse(@NonNull Call<Result<List<Room>>> call, @NonNull Response<Result<List<Room>>> response) {
                if(response.isSuccessful()) {
                    Result<List<Room>> result = response.body();
                    if(result != null) {
                        List<Room> roomList = result.getResult();
                        if(roomList.size() != 0) {
                            for (Room room : roomList) {
                                if(room.getHome() == null) {
                                    // The Home containing this room was deleted! We must delete this Room
                                    deleteUselessRoom(room, v);
                                } else {
                                    if(room.getHome().getId().equals(homeId)) {
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
                    Snackbar.make(v, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull Call<Result<List<Room>>> call, @NonNull Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    /* this method deletes a Room which has no Home, therefore its useless in our App */
    private void deleteUselessRoom(Room r, View v) {
        api.deleteRoom(r.getId(), new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                if(response.isSuccessful()) {
                    Result<Boolean> result = response.body();
                    if(result == null || !result.getResult())
                        Snackbar.make(v, "No se pudo eliminar una Room sin padre", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(v, "No se pudo eliminar una Room sin padre", Snackbar.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                Snackbar.make(v, "No se pudo eliminar una Room sin padre", Snackbar.LENGTH_LONG).show();
                handleUnexpectedError(t);
            }
        });
    }

    private void handleUnexpectedError(@NonNull Throwable t) {
        String LOG_TAG = "com.example.ultrahome";
        Log.e(LOG_TAG, t.toString());
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
