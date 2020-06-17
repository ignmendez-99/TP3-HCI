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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.Home;
import com.example.ultrahome.apiConnection.Result;
import com.example.ultrahome.apiConnection.Room;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

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
        DevicesViewModel model = new ViewModelProvider(requireActivity()).get(DevicesViewModel.class);
        homeId = model.getText().getValue();

        // loads on screen all rooms
        getAllRoomsOfThisHome(view);

        addNewRoomButton = view.findViewById(R.id.button_add_room);
        addNewRoomButton.setOnClickListener(this::addNewRoom);

        removeRoomButton = view.findViewById(R.id.button_remove_room);
        removeRoomButton.setOnClickListener(this::deleteRoom);

        recyclerView = view.findViewById(R.id.rooms_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RoomsAdapter(getContext(), roomNames);
        recyclerView.setAdapter(adapter);
    }

    private void addNewRoom(View v) {
        String name = "Cocina de Nacho"; // TODO: HARDCODEADO -> EL USUARIO DEBE ELEGIR EL NOMBRE
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
        if(roomNames.size() != 0) {  /* ESTE IF DESPUES HAY QUE SACARLO, YA QUE EN LA REALIDAD,
                                        EL BOTON DE ELIMINAR ROOMS ESTA SOLAMENTE SI EXISTEN ROOMS */

            // TODO: este bloque tambien esta hardcodeado, ya que siempre saca de pantalla la ultima ROOM creada
            int positionLastRoom = roomNames.size() - 1;
            String roomNameToRemove = roomNames.get(positionLastRoom);
            roomNamesBackupBeforeDeleting.add(roomNameToRemove);
            roomNames.remove(positionLastRoom);
            adapter.notifyItemRemoved(positionLastRoom);

            deletingRoomSnackbar = Snackbar.make(view, "Room deleted!", Snackbar.LENGTH_SHORT);
            deletingRoomSnackbar.setAction("UNDO", new UndoDeleteRoomListener());
            deletingRoomSnackbar.addCallback(new DeleteRoomSnackbarTimeout(view));
            deletingRoomSnackbar.show();
        }
    }

    private void getAllRoomsOfThisHome(View v) {
        api.getRoomsInThisHome(homeId, new Callback<Result<List<Room>>>() {
            @Override
            public void onResponse(@NonNull Call<Result<List<Room>>> call, @NonNull Response<Result<List<Room>>> response) {
                if(response.isSuccessful()) {
                    Result<List<Room>> result = response.body();
                    if(result != null) {
                        List<Room> roomList = result.getResult();
                        if(roomList.size() != 0) {
                            for (Room h : roomList) {
                                roomIds.add(h.getId());
                                roomNames.add(h.getName());
                                adapter.notifyItemInserted(roomNames.size() - 1);
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

    private void handleUnexpectedError(@NonNull Throwable t) {
        String LOG_TAG = "com.example.ultrahome";
        Log.e(LOG_TAG, t.toString());
    }

    
    private class UndoDeleteRoomListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO: HARDCODEADO, YA QUE SIEMPRE AGARRA LA PRIMERA POSITION
            String roomToRetrieve = roomNamesBackupBeforeDeleting.get(0);
            roomNamesBackupBeforeDeleting.remove(0);
            roomNames.add(roomToRetrieve);
            adapter.notifyItemInserted(0);
            deletingRoomSnackbar.dismiss();
        }
    }


    private class DeleteRoomSnackbarTimeout extends BaseTransientBottomBar.BaseCallback<Snackbar> {
        private View view;

        DeleteRoomSnackbarTimeout(View v) {
            view = v;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(event == DISMISS_EVENT_TIMEOUT) {
                super.onDismissed(transientBottomBar, event);
                int positionOfLastRoom = roomIds.size() - 1; // TODO: HARDCODED, AS IT ALWAYS DELETES THE LAST ROOM IN THE LIST

                api.deleteRoom(roomIds.get(positionOfLastRoom), new Callback<Result<Boolean>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                        if (response.isSuccessful()) {
                            Result<Boolean> result = response.body();
                            if (result != null && result.getResult()) {
                                roomIds.remove(positionOfLastRoom);
                                roomNamesBackupBeforeDeleting.remove(positionOfLastRoom);
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
