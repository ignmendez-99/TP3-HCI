package com.example.ultrahome.ui.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class DevicesFragment extends Fragment {

    private Button button;
    private RecyclerView recyclerView;
    private List<String> homesList;
    private LinearLayoutManager layoutManager;
    private HomesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_devices, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homesList = new ArrayList<>();

        /* BORRAR DESPUES */
        homesList.add("Casa de Nacho");
        homesList.add("Casa de papa");

        button = view.findViewById(R.id.button_add_home);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add items to a RecyclerView programmatically
                homesList.add("AGREGO");
                adapter.notifyItemInserted(homesList.size() - 1);
                Snackbar.make(v, "Home Added!", Snackbar.LENGTH_SHORT).show();
            }
        });

        recyclerView = view.findViewById(R.id.homes_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomesAdapter(getContext(), homesList);
        recyclerView.setAdapter(adapter);

    }
}
