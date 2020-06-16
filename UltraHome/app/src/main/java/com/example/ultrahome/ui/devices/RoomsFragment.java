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

public class RoomsFragment extends Fragment {

    private Button button;
    private RecyclerView recyclerView;
    private List<String> roomsList;
    private LinearLayoutManager layoutManager;
    private RoomsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rooms, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomsList = new ArrayList<>();

        /* BORRAR DESPUES */
        roomsList.add("HABITACION");
        roomsList.add("COCINA");

        button = view.findViewById(R.id.button_add_room);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add items to a RecyclerView programmatically
                roomsList.add("AGREGO");
                adapter.notifyItemInserted(roomsList.size() - 1);
                Snackbar.make(v, "Room Added!", Snackbar.LENGTH_SHORT).show();
            }
        });

        recyclerView = view.findViewById(R.id.rooms_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RoomsAdapter(getContext(), roomsList);
        recyclerView.setAdapter(adapter);
    }
}
