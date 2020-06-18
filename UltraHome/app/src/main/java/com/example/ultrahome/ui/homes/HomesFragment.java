package com.example.ultrahome.ui.homes;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.entities.Error;
import com.example.ultrahome.apiConnection.entities.Home;
import com.example.ultrahome.apiConnection.entities.Result;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomesFragment extends Fragment {

    // Screen controls
    private Button buttonAddHome;

    // variables for dealing with the RecyclerView
    private RecyclerView recyclerView;
    private Integer positionToDelete;
    private LinearLayoutManager layoutManager;
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

        // Displays in screen all Homes -->  todo: FALTA CACHE, ya que sino puede ser mucha carga?
        getAllHomes(view);

        buttonAddHome = view.findViewById(R.id.button_add_home);
        buttonAddHome.setOnClickListener(this::addNewHome);

        recyclerView = view.findViewById(R.id.homes_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomesAdapter(getContext(), homeNames, this);
        recyclerView.setAdapter(adapter);

        // Swipe to delete functionality is assigned here
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteHomeCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    List<String> getIdList() {
        return homeIds;
    }

    void navigateToRoomsFragment(View view) {
        final NavController navController =  Navigation.findNavController(view);
        navController.navigate(R.id.roomsFragment);
    }

    private void addNewHome(View v) {
        String name = "Casa de Nacho " + new Random().nextInt(10000); // TODO: HARDCODEADO -> EL USUARIO DEBE ELEGIR EL NOMBRE
        Home newHome = new Home(name);
        api.addHome(newHome, new Callback<Result<Home>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Home>> call, @NonNull Response<Result<Home>> response) {
                if(response.isSuccessful()) {
                    Result<Home> result = response.body();
                    if(result != null) {
                        homeIds.add(result.getResult().getId());
                        homeNames.add(name);
                        adapter.notifyItemInserted(homeNames.size() - 1);
                        Snackbar.make(v, "Home Added!", Snackbar.LENGTH_SHORT).show();
                    } else
                        Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                } else
                    handleError(response);
            }
            @Override
            public void onFailure(@NonNull Call<Result<Home>> call, @NonNull Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    private void deleteHome(View v) {
        // remove the Home Card from screen
        String homeNameToRemove = homeNames.get(positionToDelete);
        homeNamesBackupBeforeDeleting.add(homeNameToRemove);
        homeNames.remove(positionToDelete.intValue());
        adapter.notifyItemRemoved(positionToDelete);

        deletingHomeSnackbar = Snackbar.make(v, "Home deleted!", Snackbar.LENGTH_SHORT);
        deletingHomeSnackbar.setAction("UNDO", new UndoDeleteHomeListener());
        deletingHomeSnackbar.addCallback(new DeleteHomeSnackbarTimeout(v));
        deletingHomeSnackbar.show();
    }

    public void deleteHome(View v, int position) {
        positionToDelete = position;
        deleteHome(v);
    }

    private void getAllHomes(View v) {
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
                    handleError(response);
            }

            @Override
            public void onFailure(@NonNull Call<Result<List<Home>>> call, @NonNull Throwable t) {
                handleUnexpectedError(t);
            }
        });
    }

    private <T> void handleError(@NonNull Response<T> response) {
        Error error = ApiClient.getInstance().getError(response.errorBody());
        String text = "ERROR" + error.getDescription().get(0) + error.getCode();
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    private static void handleUnexpectedError(@NonNull Throwable t) {
        String LOG_TAG = "com.example.ultrahome";
        Log.e(LOG_TAG, t.toString());
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
                            handleError(response);
                    }
                    @Override
                    public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                        handleUnexpectedError(t);
                    }
                });
            }
        }
    }
}
