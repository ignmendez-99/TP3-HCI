package com.example.ultrahome.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ultrahome.R;
import com.example.ultrahome.ui.devices.DevicesListFragment;
import com.example.ultrahome.ui.homes.HomesFragment;
import com.example.ultrahome.ui.rooms.RoomsFragment;

public class TabletFragment extends Fragment {

    private HomesFragment fragment_left;
    private RoomsFragment fragment_middle;
    private DevicesListFragment fragment_right;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tablet_main_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentTransaction transaction;

        fragment_left = new HomesFragment();
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tablet_1, fragment_left).commit();

        fragment_middle = new RoomsFragment();
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tablet_2, fragment_middle).commit();

        fragment_right = new DevicesListFragment();
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tablet_3, fragment_right).commit();
    }
}
