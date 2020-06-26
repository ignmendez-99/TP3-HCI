package com.example.ultrahome.ui.homes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
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
import com.example.ultrahome.apiConnection.entities.Room;
import com.example.ultrahome.ui.devices.DevicesListFragment;
import com.example.ultrahome.ui.rooms.RoomsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.ContextCompat.getSystemService;
import static java.security.AccessController.getContext;

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
    private Map<Integer, Integer> roomsInEachHome;

    private Snackbar deletingHomeSnackbar;
    private boolean deletingHome = false;
    private boolean fragmentOnScreen = true;
    private ApiClient api;

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
        roomsInEachHome = new HashMap<>();

        buttonAddHome = view.findViewById(R.id.button_show_AddHomeDialog);
        buttonAddHome.setOnClickListener(this::showAddHomeDialog);

        setupRecyclerView(view);

        // If there is a savedState, we retrieve it and we DON'T call the API.
        if(savedInstanceState != null) {
            recoverSavedState(savedInstanceState, view);
        } else {
            getAllHomes(view);
        }

        // Swipe to delete functionality is assigned here
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteHomeCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "123name";
            String description = "123desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(getContext(), NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupRecyclerView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.homes_recycler_view);
        if(recyclerView == null) {
            // if we couldn't find the Linear version, the we must be in Landscape mode
            recyclerView = view.findViewById(R.id.homes_recycler_view_grid);
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new HomesAdapterGrid(getContext(), homeNames,this);
        } else {
            // Portrait mode
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new HomesAdapterLinear(getContext(), homeNames, this);
        }
        recyclerView.setAdapter(adapter);
    }

    private void recoverSavedState(@NonNull Bundle savedInstanceState, View view) {
        int numberOfHomesSaved = savedInstanceState.getInt("numberOfHomes");
        for(int i = 0; i < numberOfHomesSaved; i++) {
            homeNames.add(savedInstanceState.getString("homeName" + i));
            homeIds.add(savedInstanceState.getString("homeId" + i));
            adapter.notifyItemInserted(i);
        }
        if(numberOfHomesSaved == 0) {
            view.findViewById(R.id.zero_homes).setVisibility(View.VISIBLE);
        }
        requireView().findViewById(R.id.button_show_AddHomeDialog).setVisibility(View.VISIBLE);
        requireView().findViewById(R.id.loadingHomesList).setVisibility(View.GONE);

        HomesViewModel model = new ViewModelProvider(requireActivity()).get(HomesViewModel.class);
        roomsInEachHome = model.getRoomsInEachHome().getValue();
        if(roomsInEachHome != null) {
            for (Integer position : roomsInEachHome.keySet()) {
                adapter.notifyNumberOfRoomsRetrieved(position, roomsInEachHome.get(position));
                adapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentOnScreen = false;
        HomesViewModel model = new ViewModelProvider(requireActivity()).get(HomesViewModel.class);
        model.storeRoomsInEachHome(roomsInEachHome);
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
        // todo: try to replace this with a Bundle/Intent
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
        requireView().findViewById(R.id.zero_homes).setVisibility(View.GONE);
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
        deletingHome = true;
        deletingHomeSnackbar.addCallback(new DeleteHomeSnackbarTimeout());
        deletingHomeSnackbar.show();
    }

    /* this method just puts the ""removed"" Home back on screen */
    void recoverRemovedHome(View v) {
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
                                String homeId = h.getId();
                                homeIds.add(homeId);
                                homeNames.add(h.getName());
                                getAmountOfRoomsInThisHome(homeId, homeNames.size() - 1);
                                adapter.notifyItemInserted(homeNames.size() - 1);
                            }
                            if(homeList.size() == 0)
                                v.findViewById(R.id.zero_homes).setVisibility(View.VISIBLE);
                            else
                                v.findViewById(R.id.zero_homes).setVisibility(View.GONE);
                            v.findViewById(R.id.button_show_AddHomeDialog).setVisibility(View.VISIBLE);
                        } else {
                            ErrorHandler.logError(response);
                            if(fragmentOnScreen)
                                showGetHomesError();
                        }
                    } else {
                        ErrorHandler.logError(response);
                        if(fragmentOnScreen)
                            showGetHomesError();
                    }
                    v.findViewById(R.id.loadingHomesList).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<Result<List<Home>>> call, @NonNull Throwable t) {
                    if(fragmentOnScreen) {
                        showGetHomesError();
                        ErrorHandler.handleUnexpectedError(t, requireView(), HomesFragment.this);
                    }
                }
            });
        }).start();
    }

    /* For updating the TextView that says "3 rooms inside" */
    private void getAmountOfRoomsInThisHome(String homeId, int positionToChange) {
        new Thread(() -> {
           api.getRoomsInThisHome(homeId, new Callback<Result<List<Room>>>() {
               @Override
               public void onResponse(@NonNull Call<Result<List<Room>>> call, @NonNull Response<Result<List<Room>>> response) {
                   if(response.isSuccessful()) {
                       Result<List<Room>> result = response.body();
                       if(result != null) {
                           List<Room> listOfRooms = result.getResult();
                           int numberOfRooms = listOfRooms.size();
                           roomsInEachHome.put(positionToChange, numberOfRooms);
                           adapter.notifyNumberOfRoomsRetrieved(positionToChange, numberOfRooms);
                           adapter.notifyItemChanged(positionToChange);
                       } else {
                           ErrorHandler.logError(response);
                       }
                   } else {
                       ErrorHandler.logError(response);
                   }
               }

               @Override
               public void onFailure(@NonNull Call<Result<List<Room>>> call, @NonNull Throwable t) {
                   if(fragmentOnScreen)
                       ErrorHandler.handleUnexpectedError(t, requireView(), HomesFragment.this);
               }
           });
        }).start();
    }

    private void showGetHomesError() {
        requireView().findViewById(R.id.get_homes_failed).setVisibility(View.VISIBLE);
        requireView().findViewById(R.id.button_get_homes_again).setOnClickListener(HomesFragment.this::getHomesAgain);
        requireView().findViewById(R.id.loadingHomesList).setVisibility(View.GONE);
    }

    private void showDeleteHomeError() {
        Snackbar s = Snackbar.make(requireView(), "Could not delete Home!", Snackbar.LENGTH_SHORT);
        s.setAction("CLOSE", HomesFragment.this::recoverRemovedHome);
        s.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                HomesFragment.this.recoverRemovedHome(HomesFragment.this.getView());
            }
        });
        s.show();
    }

    private void getHomesAgain(View v) {
        requireView().findViewById(R.id.get_homes_failed).setVisibility(View.GONE);
        requireView().findViewById(R.id.loadingHomesList).setVisibility(View.VISIBLE);
        getAllHomes(requireView());
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

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if(deletingHome) {
                deletingHome = false;
                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
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
                                        if(homeIds.size() == 0 && fragmentOnScreen)
                                            HomesFragment.this.requireView().findViewById(R.id.zero_homes).setVisibility(View.VISIBLE);
                                    } else {
                                        ErrorHandler.logError(response);
                                        if(fragmentOnScreen)
                                            showDeleteHomeError();
                                    }
                                } else {
                                    ErrorHandler.logError(response);
                                    if(fragmentOnScreen)
                                        showDeleteHomeError();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                                if(fragmentOnScreen)
                                    showDeleteHomeError();
                            }
                        });
                    }).start();
                }
            }
        }
    }
}
