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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.example.ultrahome.apiConnection.ApiClient;
import com.example.ultrahome.apiConnection.Home;
import com.example.ultrahome.apiConnection.Result;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevicesFragment extends Fragment {

    private Button buttonAddHome;
    private Button buttonRemoveHome;
    private RecyclerView recyclerView;
    private List<String> homeNames;
    private List<String> homeIds;
    private LinearLayoutManager layoutManager;
    private HomesAdapter adapter;
    private ApiClient api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = ApiClient.getInstance();
        homeNames = new ArrayList<>();
        homeIds = new ArrayList<>();

        getAllHomes(view);

        buttonAddHome = view.findViewById(R.id.button_add_home);
        buttonAddHome.setOnClickListener(this::addNewHome);

        buttonRemoveHome = view.findViewById(R.id.button_remove_home);
        buttonRemoveHome.setOnClickListener(this::deleteHome);

        recyclerView = view.findViewById(R.id.homes_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomesAdapter(getContext(), homeNames);
        recyclerView.setAdapter(adapter);

    }

    private void addNewHome(View v) {
        // ESTE STRING HAY QUE OBTENERLO CON UN POPUP
        String name = "Casa de Nacho"; // HARDCODEADOOOOO
        Home newHome = new Home(name, new Home.HomeMeta("6m2"));
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
                    Snackbar.make(v, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull Call<Result<Home>> call, @NonNull Throwable t) {
                Snackbar.make(v, "ERROR tipo 3", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteHome(View v) {
        if(homeNames.size() != 0) {
            int positionOfLastHome = homeIds.size() - 1;  // THIS HARDCODED, AS IT ALWAYS DELETES THE LAST HOME IN THE LIST
            api.deleteHome(homeIds.get(positionOfLastHome), new Callback<Result<Boolean>>() {
                @Override
                public void onResponse(@NonNull Call<Result<Boolean>> call, @NonNull Response<Result<Boolean>> response) {
                    if(response.isSuccessful()) {
                        Result<Boolean> result = response.body();
                        if(result != null && result.getResult()) {
                            adapter.notifyItemRemoved(positionOfLastHome);
                            homeIds.remove(positionOfLastHome);
                            homeNames.remove(positionOfLastHome);
                            Snackbar snackbar = Snackbar.make(v, "Home deleted!", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("UNDO", new UndoDeleteHomeListener());
                            
                            snackbar.show();
                        } else
                            Snackbar.make(v, "ERROR tipo 1", Snackbar.LENGTH_LONG).show();
                    } else
                        Snackbar.make(v, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(@NonNull Call<Result<Boolean>> call, @NonNull Throwable t) {
                    Snackbar.make(v, "ERROR tipo 3", Snackbar.LENGTH_LONG).show();
                }
            });
        }
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
                    Snackbar.make(v, "ERROR tipo 2", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<Result<List<Home>>> call, @NonNull Throwable t) {
                handleUnexpectedError(t);
                //Snackbar.make(v, "ERROR tipo 3", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void handleUnexpectedError(Throwable t) {
        String LOG_TAG = "com.example.ultrahome";
        Log.e(LOG_TAG, t.toString());
    }

    public class UndoDeleteHomeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "HAGO UN UNDO JAJA", Toast.LENGTH_SHORT).show();
        }
    }
}
