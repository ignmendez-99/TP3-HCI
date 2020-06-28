package com.example.ultrahome.ui.routines;

import android.app.Dialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


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
import com.example.ultrahome.apiConnection.entities.Result;
import com.example.ultrahome.apiConnection.entities.Routine.ActionsItem;
import com.example.ultrahome.apiConnection.entities.Routine.Routine;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutinesFragment extends Fragment {

    // variables for dealing with the RecyclerView
    private RecyclerView recyclerView;
    private Integer positionToDelete;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private RoutinesAdapter adapter;

    private List<String> routineNames;
    private List<String> routineIds;
    private List<String> routineNamesBackupBeforeDeleting;
    private List<Routine> routines;
    private List<String> auxList;

    private ApiClient api;
    private boolean deletingRoutine = false;
    private boolean fragmentOnScreen = true;
    private Snackbar deletingRoutineSnackbar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        routines = new ArrayList<>();
        routineIds = new ArrayList<>();
        routineNames = new ArrayList<>();
        routineNamesBackupBeforeDeleting = new ArrayList<>();
        api = ApiClient.getInstance();

        setupRecyclerView(view);

        if(savedInstanceState != null) {
            recoverSavedState(savedInstanceState, view);
        } else {
            getRoutines(view);
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteRoutineCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupRecyclerView(@NonNull View view) {
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
    }

    private void recoverSavedState(@NonNull Bundle savedInstanceState, View view) {
        int numberOfRoutinesSaved = savedInstanceState.getInt("numberOfRoutines");
        for(int i = 0; i < numberOfRoutinesSaved; i++) {
            routineNames.add(savedInstanceState.getString("routineName" + i));
            routineIds.add(savedInstanceState.getString("routineId" + i));
            routines = (ArrayList<Routine>)savedInstanceState.getSerializable("key");
            adapter.notifyItemInserted(i);
        }

        if(numberOfRoutinesSaved == 0) {
            view.findViewById(R.id.zero_routines).setVisibility(View.VISIBLE);
        }
        requireView().findViewById(R.id.loadingRoutinesList).setVisibility(View.GONE);
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

        if(routineNames != null) {
            outState.putInt("numberOfRoutines", routineNames.size());
            for (int i = 0; i < routineNames.size(); i++) {
                outState.putString("routineName" + i, routineNames.get(i));
                outState.putString("routineId" + i, routineIds.get(i));
                outState.putSerializable("key", (Serializable) routines);
            }
        }
    }

    void deleteRoutine(View v) {
        deletingRoutineSnackbar = Snackbar.make(v, getContext().getString(R.string.routine_deleted_string), Snackbar.LENGTH_SHORT);
        deletingRoutineSnackbar.setAction(getContext().getString(R.string.undo_string), new UndoDeleteRoutineListener());
        deletingRoutine = true;
        deletingRoutineSnackbar.addCallback(new DeleteRoutineSnackbarTimeout());
        deletingRoutineSnackbar.show();
    }

    /* this method just puts the ""removed"" Routine back on screen */
    void recoverRemovedRoutine(View v) {
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

    /* The only thing that the UNDO action does, is closing the Snackbar and putting the
       Routine on screen again */
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

    private void showGetRoutinesError() {
        requireView().findViewById(R.id.get_routines_failed).setVisibility(View.VISIBLE);
        requireView().findViewById(R.id.button_get_routines_again).setOnClickListener(RoutinesFragment.this::getRoutinesAgain);
        requireView().findViewById(R.id.loadingRoutinesList).setVisibility(View.GONE);
    }

    private void showDeleteRoutineError() {
        Snackbar s = Snackbar.make(requireView(), getContext().getString(R.string.couldnt_delete_routine_string), Snackbar.LENGTH_SHORT);
        s.setAction(getContext().getString(R.string.close_lowercase_string), RoutinesFragment.this::recoverRemovedRoutine);
        s.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                RoutinesFragment.this.recoverRemovedRoutine(RoutinesFragment.this.getView());
            }
        });
        s.show();
    }

    private void getRoutinesAgain(View v) {
        requireView().findViewById(R.id.get_routines_failed).setVisibility(View.GONE);
        requireView().findViewById(R.id.loadingRoutinesList).setVisibility(View.VISIBLE);
        getRoutines(requireView());
    }

    void setDialogRoutine(View v, int position){

        //First create the dialog
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.routine_description_dialog);

        //instance everything
        TextView title = dialog.findViewById(R.id.routine_title);
        TextView actionsList = dialog.findViewById(R.id.routine_actions_list);
        TextView executeFail = dialog.findViewById(R.id.execute_routine_fail);
        Button closeButton = dialog.findViewById(R.id.close_button);
        Button executeButton = dialog.findViewById(R.id.execute_button);

        String nameRoutineClicked = routineNames.get(position);
        String idRoutineClicked = routineIds.get(position);

        auxList = new ArrayList<>();

        //build the description string
        String routineDescription = "";
        Routine routineClicked = routines.get(position);
        for(ActionsItem actions : routineClicked.getActions()){
            String deviceName = actions.getDevice().getName();
            if(deviceName != null){
                String actionName = actions.getActionName();
                String auxString = "- " + deviceName + " -> " + actionName;
                routineDescription = routineDescription + "\n" + auxString;
                auxList.add(auxString);
            }
            else{
                executeFail.setVisibility(View.VISIBLE);
                executeButton.setEnabled(false);
            }
        }

        SpannableString content = new SpannableString(nameRoutineClicked);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        title.setText(content);
        actionsList.setText(routineDescription);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        executeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeRoutine(v, idRoutineClicked, dialog);
            }
        });
        dialog.show();
    }

    private void updateDescription(List<String> list, Dialog d){
        TextView tv = d.findViewById(R.id.routine_actions_list);
        String newDescription = "\n";
        String resultString="";
        for(int i=0; i < list.size(); i++){
            if(list.get(i)==null || list.get(i)=="false"){
                resultString = auxList.get(i) + " " + getContext().getString(R.string.failed_string) + "\n";
            }
            else {
                resultString = auxList.get(i) + " " + getContext().getString(R.string.success_string) + "\n";
            }
            newDescription = newDescription + resultString;
        }
        tv.setText(newDescription);
    }

    private void getRoutines(View v) {
        new Thread(() -> {
            api.getRoutines(new Callback<Result<List<Routine>>>() {
                @Override
                public void onResponse(@NonNull Call<Result<List<Routine>>> call, @NonNull Response<Result<List<Routine>>> response) {
                    if (response.isSuccessful()) {
                        Result<List<Routine>> result = response.body();
                        if (result != null) {
                            List<Routine> routineList = result.getResult();
                            for (Routine h : routineList) {
                                routineIds.add(h.getId());
                                routineNames.add(h.getName());
                                routines.add(h);
                                adapter.notifyItemInserted(routineNames.size() - 1);
                            }
                            if (routineList.size() == 0)
                                v.findViewById(R.id.zero_routines).setVisibility(View.VISIBLE);
                            else
                                v.findViewById(R.id.zero_routines).setVisibility(View.GONE);
                        } else {
                            ErrorHandler.logError(response);
                            if (fragmentOnScreen)
                                showGetRoutinesError();
                        }
                    } else {
                        ErrorHandler.logError(response);
                        if (fragmentOnScreen)
                            showGetRoutinesError();
                    }
                    v.findViewById(R.id.loadingRoutinesList).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Routine>>> call, @NonNull Throwable t) {
                    if (fragmentOnScreen) {
                        showGetRoutinesError();
                        ErrorHandler.handleUnexpectedError(t, requireView(), RoutinesFragment.this);
                    }
                }
            });
        }).start();
    }

    private void executeRoutine(View v, String routineId, Dialog d){
        new Thread(() -> {
            api.executeRoutine(routineId, new Callback<Result<List<String>>>() {
                @Override
                public void onResponse(@NonNull Call<Result<List<String>>> call, @NonNull Response<Result<List<String>>> response) {
                    if (response.isSuccessful()) {
                        Result<List<String>> result = response.body();
                        if (result != null) {
                            List<String> aux = new ArrayList<>();
                            List<String> resultRoutine = result.getResult();
                            updateDescription(resultRoutine, d);
                            Snackbar.make(requireView(), getContext().getString(R.string.routine_executed_string), Snackbar.LENGTH_SHORT).show();
                        } else {
                            ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                        }
                    } else {
                        ErrorHandler.handleError(response, requireView(), getString(R.string.error_1_string));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<String>>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t, requireView(), RoutinesFragment.this);
                }
            });
        }).start();
    }

    private class DeleteRoutineSnackbarTimeout extends BaseTransientBottomBar.BaseCallback<Snackbar> {

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(deletingRoutine) {
                deletingRoutine = false;
                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                    super.onDismissed(transientBottomBar, event);
                    new Thread(() -> {
                        api.deleteRoutine(routineIds.get(positionToDelete), new Callback<Result<Boolean>>() {
                            @Override
                            public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                                if (response.isSuccessful()) {
                                    Result<Boolean> result = response.body();
                                    if (result != null && result.getResult()) {
                                        routineIds.remove(positionToDelete.intValue());
                                        routineNamesBackupBeforeDeleting.remove(0);
                                        if(routineIds.size() == 0 && fragmentOnScreen)
                                            RoutinesFragment.this.requireView().findViewById(R.id.zero_routines).setVisibility(View.VISIBLE);
                                    } else {
                                        ErrorHandler.logError(response);
                                        if(fragmentOnScreen)
                                            showDeleteRoutineError();
                                    }
                                } else {
                                    ErrorHandler.logError(response);
                                    if(fragmentOnScreen)
                                        showDeleteRoutineError();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                                if(fragmentOnScreen)
                                    showDeleteRoutineError();
                            }
                        });
                    }).start();
                }
            }
        }
    }

}
