package com.example.ultrahome.ui.homes;

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
import com.example.ultrahome.apiConnection.entities.Home;
import com.example.ultrahome.apiConnection.entities.Result;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomesFragment extends Fragment{

    // Screen controls
    private FloatingActionButton buttonAddHome;

    // variables for dealing with the RecyclerView
    private RecyclerView recyclerView;
    private Integer positionToDelete;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private HomesAdapter adapter;

    private List<String> homeNames;
    private List<String> homeIds;
    private List<String> homeNamesBackupBeforeDeleting;

    private Snackbar deletingHomeSnackbar;
    private static ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = ApiClient.getInstance();
        homeNames = new ArrayList<>();
        homeIds = new ArrayList<>();
        homeNamesBackupBeforeDeleting = new ArrayList<>();

        buttonAddHome = view.findViewById(R.id.button_show_AddHomeDialog);
        buttonAddHome.setOnClickListener(this::showAddHomeDialog);

        recyclerView = view.findViewById(R.id.homes_recycler_view);
        if(recyclerView == null) {
            recyclerView = view.findViewById(R.id.homes_recycler_view_grid);
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new HomesAdapterGrid(getContext(), homeNames,this);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new HomesAdapterLinear(getContext(), homeNames, this);
        }
        recyclerView.setAdapter(adapter);

        // If there is a savedState, we retrieve it and we DON'T call the API.
        if(savedInstanceState != null) {
            int numberOfHomesSaved = savedInstanceState.getInt("numberOfHomes");
            for(int i = 0; i < numberOfHomesSaved; i++) {
                homeNames.add(savedInstanceState.getString("homeName" + i));
                homeIds.add(savedInstanceState.getString("homeId" + i));
                adapter.notifyItemInserted(i);
            }
        } else {
            getAllHomes(view);
        }

        // Swipe to delete functionality is assigned here
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteHomeCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);

        if(homeNames != null) {
            outState.putInt("numberOfHomes", homeNames.size());
            for (int i = 0; i < homeNames.size(); i++) {
                outState.putString("homeName" + i, homeNames.get(i));
                outState.putString("homeId" + i, homeIds.get(i));
            }
        }
    }

    /* Called by HomesAdapter, when a Card is clicked */
    void navigateToRoomsFragment(View view, int position) {
        // we send the homeId to the RoomsFragment, so that the correct Rooms are loaded
        String idOfHomeClicked = homeIds.get(position);
        HomeToRoomViewModel model = new ViewModelProvider(requireActivity()).get(HomeToRoomViewModel.class);
        model.storeHomeId(idOfHomeClicked);
        final NavController navController =  Navigation.findNavController(view);
        navController.navigate(R.id.action_HomesFragment_to_RoomsFragment);
    }

    /* Called by the AddHomeDialog, when the Home has been successfully added */
    void notifyNewHomeAdded(String homeId, String homeName) {
        homeIds.add(homeId);
        homeNames.add(homeName);
        adapter.notifyItemInserted(homeNames.size() - 1);
        Snackbar.make(this.requireView(), "Home Added!", Snackbar.LENGTH_SHORT).show();
    }

    private void showAddHomeDialog(View v) {
        // Create and show the dialog.
        AddHomeDialog addHomeDialog = new AddHomeDialog(requireContext(), this);
        addHomeDialog.show();
    }

    void deleteHome(View v) {
        deletingHomeSnackbar = Snackbar.make(v, "Home deleted!", Snackbar.LENGTH_SHORT);
        deletingHomeSnackbar.setAction("UNDO", new UndoDeleteHomeListener());
        deletingHomeSnackbar.addCallback(new DeleteHomeSnackbarTimeout(v));
        deletingHomeSnackbar.show();
    }

    /* this method just puts the ""removed"" Home back on screen */
    void recoverRemovedHome() {
        String homeToRetrieve = homeNamesBackupBeforeDeleting.get(0);
        homeNamesBackupBeforeDeleting.remove(0);
        homeNames.add(positionToDelete, homeToRetrieve);
        adapter.notifyItemInserted(positionToDelete);
    }

    void showDeleteHomeDialog(int position) {
        positionToDelete = position;

        // remove the Home Card from screen
        String homeNameToRemove = homeNames.get(positionToDelete);
        homeNamesBackupBeforeDeleting.add(homeNameToRemove);
        homeNames.remove(positionToDelete.intValue());
        adapter.notifyItemRemoved(positionToDelete);
        adapter.notifyItemRangeChanged(positionToDelete, homeNames.size());

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        // Create and show the dialog.
        DeleteHomeConfirmationDialog newFragment = new DeleteHomeConfirmationDialog(this);
        newFragment.show(ft, "dialog");
    }

    private void getAllHomes(View v) {
        new Thread(() -> {
            api.getHomes(new Callback<Result<List<Home>>>() {
                @Override
                public void onResponse(@NonNull Call<Result<List<Home>>> call, @NonNull Response<Result<List<Home>>> response) {
                    if(response.isSuccessful()) {
                        Result<List<Home>> result = response.body();
                        if(result != null) {
                            List<Home> homeList = result.getResult();
                            for (Home h: homeList) {
                                homeIds.add(h.getId());
                                homeNames.add(h.getName());
                                adapter.notifyItemInserted(homeNames.size() - 1);
                            }
                        } else
                            Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                    } else
                        ErrorHandler.handleError(response, getContext());
                    v.findViewById(R.id.loadingHomesList).setVisibility(View.GONE);
                    v.findViewById(R.id.button_show_AddHomeDialog).setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Home>>> call, @NonNull Throwable t) {
                    ErrorHandler.handleUnexpectedError(t);
                    v.findViewById(R.id.loadingHomesList).setVisibility(View.GONE);
                    v.findViewById(R.id.button_show_AddHomeDialog).setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }


    /* The only thing that the UNDO action does, is closing the Snackbar and putting the
       home on screen again */
    private class UndoDeleteHomeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String homeToRetrieve = homeNamesBackupBeforeDeleting.get(0);
            homeNamesBackupBeforeDeleting.remove(0);
            homeNames.add(positionToDelete, homeToRetrieve);
            adapter.notifyItemInserted(positionToDelete);
            deletingHomeSnackbar.dismiss();
        }
    }


    /* In the moment that the delete-home-snackbar disappears, the Home is deleted from DataBase */
    private class DeleteHomeSnackbarTimeout extends BaseTransientBottomBar.BaseCallback<Snackbar> {
        private View view;

        DeleteHomeSnackbarTimeout(View v) {
            view = v;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                super.onDismissed(transientBottomBar, event);
                new Thread(() -> {
                    api.deleteHome(homeIds.get(positionToDelete), new Callback<Result<Boolean>>() {
                        @Override
                        public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                            if (response.isSuccessful()) {
                                Result<Boolean> result = response.body();
                                if (result != null && result.getResult()) {
                                    homeIds.remove(positionToDelete.intValue());
                                    homeNamesBackupBeforeDeleting.remove(0);
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
