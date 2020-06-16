package com.example.ultrahome.ui.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrahome.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class DevicesListFragment extends Fragment {

    Button button;
    private RecyclerView recyclerView;
    private List<String> devicesList;
    private LinearLayoutManager layoutManager;
    private DevicesListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_devices_list, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        devicesList = new ArrayList<>();

        /* BORRAR DESPUES */
        devicesList.add("Faucet de paula");
        devicesList.add("Mi cortina");


        recyclerView = view.findViewById(R.id.horizontal_devices_recycler_view);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DevicesListAdapter(getContext(), devicesList);
        recyclerView.setAdapter(adapter);

        insertNestedFragment();
    }

    private void insertNestedFragment() {
        Fragment childFragment = new LightsControllerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.device_control_container, childFragment).commit();
    }
}
