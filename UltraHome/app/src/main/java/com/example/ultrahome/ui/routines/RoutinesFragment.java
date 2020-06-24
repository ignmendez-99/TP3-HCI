package com.example.ultrahome.ui.routines;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.ErrorHandler;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.Routine.ActionsItem;
import com.example.ultrahome.apiConnection.entities.Routine.Routine;
import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.ui.homes.HomesAdapterGrid;
import com.example.ultrahome.ui.homes.HomesAdapterLinear;
import com.example.ultrahome.ui.rooms.SwipeToDeleteRoomCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutinesFragment extends Fragment {

    private FloatingActionButton buttonAddRoutine;

    private RecyclerView recyclerView;
    private Integer positionToDelete;
    private LinearLayoutManager layoutManager;
    private LinearLayout executeEditLayout;
    private GridLayoutManager gridLayoutManager;
    private RoutinesAdapter adapter;

    private List<String> routineNames;
    private List<String> routineIds;
    private List<String> routineNamesBackupBeforeDeleting;
    private List<Routine> routines;

    private ApiClient api;
    private Snackbar deletingRoutineSnackbar;

    private LinearLayout layout;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routines, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        routineIds = new ArrayList<>();
        routineNames = new ArrayList<>();
        routineNamesBackupBeforeDeleting = new ArrayList<>();
        api = ApiClient.getInstance();

        routines = new ArrayList<>();

        buttonAddRoutine = view.findViewById(R.id.button_add_routine);

        buttonAddRoutine.setOnClickListener(this::showAddRoutineDialog);

        recyclerView = view.findViewById(R.id.routines_recycler_view);
        if(recyclerView == null) {
            recyclerView = view.findViewById(R.id.routines_recycler_view_grid);
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new RoutinesAdapterGrid(getContext(), routineNames, this);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RoutinesAdapterLinear(getContext(), routineNames, this);
        }
        recyclerView.setAdapter(adapter);

        if(savedInstanceState != null) {
            int numberOfRoomsSaved = savedInstanceState.getInt("numberOfRoutines");
            for(int i = 0; i < numberOfRoomsSaved; i++) {
                routineNames.add(savedInstanceState.getString("routineName" + i));
                routineIds.add(savedInstanceState.getString("routineId" + i));
                adapter.notifyItemInserted(i);
            }
        } else {
            getRoutines(view);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteRoutineCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void execute(View v, int position){
        String idOfRoutineClicked = routineIds.get(position);
        executeRoutine(v, idOfRoutineClicked);

    }

    public void expand(View v, TextView textview, int position){
        String idOfRoutineClicked = routineIds.get(position);
        String routineDescription = "";
        Routine routineClicked = routines.get(position);
        for(ActionsItem actions : routineClicked.getActions()){
            String deviceName = actions.getDevice().getName();
            String actionName = actions.getActionName();
            routineDescription = routineDescription + "\n" + deviceName + " --> " + actionName;
        }
        textview.setText(routineDescription);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);

        if(routineNames != null) {
            outState.putInt("numberOfRoutines", routineNames.size());
            for (int i = 0; i < routineNames.size(); i++) {
                outState.putString("routineName" + i, routineNames.get(i));
                outState.putString("routineId" + i, routineIds.get(i));
            }
        }
    }

    void notifyNewRoutineAdded(String routineId, String routineName) {
        routineIds.add(routineId);
        routineNames.add(routineName);
        adapter.notifyItemInserted(routineNames.size() - 1);
        Snackbar.make(this.requireView(), "Routine Added!", Snackbar.LENGTH_SHORT).show();
    }

    private void showAddRoutineDialog(View v) {
        // Create and show the dialog.
        AddRoutineDialog addRoutineDialog = new AddRoutineDialog(requireContext(), this);
        addRoutineDialog.show();
    }

    void deleteRoutine(View v) {
        deletingRoutineSnackbar = Snackbar.make(v, "Home deleted!", Snackbar.LENGTH_SHORT);
        deletingRoutineSnackbar.setAction("UNDO", new UndoDeleteRoutineListener());
        deletingRoutineSnackbar.addCallback(new DeleteRoutineSnackbarTimeout(v));
        deletingRoutineSnackbar.show();
    }

    /* this method just puts the ""removed"" Routine back on screen */
    void recoverRemovedRoutine() {
        String routineToRetrieve = routineNamesBackupBeforeDeleting.get(0);
        routineNamesBackupBeforeDeleting.remove(0);
        routineNames.add(positionToDelete, routineToRetrieve);
        adapter.notifyItemInserted(positionToDelete);
    }

    void showDeleteRoutineDialog(int position) {
        positionToDelete = position;

        // remove the Routine Card from screen
        String routineNameToRemove = routineNames.get(positionToDelete);
        routineNamesBackupBeforeDeleting.add(routineNameToRemove);
        routineNames.remove(positionToDelete.intValue());
        adapter.notifyItemRemoved(positionToDelete);
        adapter.notifyItemRangeChanged(positionToDelete, routineNames.size());

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        // Create and show the dialog.
        DeleteRoutineConfirmationDialog newFragment = new DeleteRoutineConfirmationDialog(this);
        newFragment.show(ft, "dialog");
    }

    private class UndoDeleteRoutineListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String routineToRetrieve = routineNamesBackupBeforeDeleting.get(0);
            routineNamesBackupBeforeDeleting.remove(0);
            routineNames.add(positionToDelete, routineToRetrieve);
            adapter.notifyItemInserted(positionToDelete);
            deletingRoutineSnackbar.dismiss();
        }
    }

    /* In the moment that the delete-room-snackbar disappears, the Room is deleted from DataBase */
    private class DeleteRoutineSnackbarTimeout extends BaseTransientBottomBar.BaseCallback<Snackbar> {
        private View view;

        DeleteRoutineSnackbarTimeout(View v) {
            view = v;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                super.onDismissed(transientBottomBar, event);
                api.deleteRoutine(routineIds.get(positionToDelete), new Callback<Result<Boolean>>() {
                    @Override
                    public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                        if (response.isSuccessful()) {
                            Result<Boolean> result = response.body();
                            if (result != null && result.getResult()) {
                                routineIds.remove(positionToDelete.intValue());
                                routineNamesBackupBeforeDeleting.remove(0);
                            } else {
                                ErrorHandler.handleError(response, requireView(), "MESSAGE");
                                // todo: falta mensaje amigable de error
                                // todo: aca se deberia volver a poner la Routine en la lista
                                // todo: (si hay dudas, miren como lo hice en el onDismissed de FragmentHomes
                            }
                        } else {
                            ErrorHandler.handleError(response, requireView(), "MESSAGE");
                            // todo: falta mensaje amigable de error
                            // todo: aca se deberia volver a poner la Routine en la lista
                            // todo: (si hay dudas, miren como lo hice en el onDismissed de FragmentHomes
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                        ErrorHandler.handleUnexpectedError(t, requireView(), RoutinesFragment.this);
                        // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
                        // todo: aca se deberia volver a poner la Routine en la lista
                        // todo: (si hay dudas, miren como lo hice en el onDismissed de FragmentHomes
                    }
                });
            }
        }
    }

    private void getRoutines(View v) {
        api.getRoutines(new Callback<Result<List<Routine>>>() {
            @Override
            public void onResponse(@NonNull Call<Result<List<Routine>>> call, @NonNull Response<Result<List<Routine>>> response) {
                if(response.isSuccessful()) {
                    Result<List<Routine>> result = response.body();
                    if(result != null) {
                        List<Routine> homeList = result.getResult();
                        for (Routine h: homeList) {
                            routineIds.add(h.getId());
                            routineNames.add(h.getName());
                            routines.add(h);
                            adapter.notifyItemInserted(routineNames.size() - 1);
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                    }
                } else {
                    ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<List<Routine>>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), RoutinesFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });
    }

    public void executeRoutine(View v, String routineId){
        api.executeRoutine(routineId, new Callback<Result<List<Boolean>>>() {
            @Override
            public void onResponse(@NonNull Call<Result<List<Boolean>>> call, @NonNull Response<Result<List<Boolean>>> response) {
                if(response.isSuccessful()) {
                    Result<List<Boolean>> result = response.body();
                    if (result != null) {
                        List<Boolean> resultRoutine = result.getResult();
                        Boolean notFail = true;
                        for (Boolean actionResult : resultRoutine) {
                            if (!actionResult) {
                                notFail = false;
                            }
                        }
                        if (notFail) {
                            Toast.makeText(getContext(), "Routine Executed", Toast.LENGTH_SHORT);
                        } else {
                            Toast.makeText(getContext(), "Routine Fail", Toast.LENGTH_SHORT);
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                    }
                }else{
                    ErrorHandler.handleError(response, requireView(), "MENSAJE");
// todo: falta poner mensaje amigable de error y PASARSELO a HandleError
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<List<Boolean>>> call, @NonNull Throwable t) {
                ErrorHandler.handleUnexpectedError(t, requireView(), RoutinesFragment.this);
                // todo: aca no va mensaje amigable, ya que la misma funcion ya lanza un Snackbar
            }
        });
    }


}
