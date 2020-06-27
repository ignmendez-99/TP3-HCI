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

        Bundle bundle = new Bundle();
        bundle.putBoolean("inTablet", true);
        fragment_left = new HomesFragment();
        fragment_left.setArguments(bundle);
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tablet_1, fragment_left).commit();
    }

    public void initRoomsFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("inTablet", true);

        if(fragment_right != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.detach(fragment_right).commit();
            fragment_right.onDestroy();
            fragment_right.onDetach();
            fragment_right = null;
        }

        if(fragment_middle != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.detach(fragment_middle).commit();
            fragment_middle.onDestroy();
            fragment_middle.onDetach();
            fragment_middle = null;
        }

        FragmentTransaction transaction;
        fragment_middle = new RoomsFragment();
        fragment_middle.setArguments(bundle);
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tablet_2, fragment_middle).commit();
    }

    public void homeWasDeleted() {
        if(fragment_right != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.detach(fragment_right).commit();
            fragment_right.onDestroy();
            fragment_right.onDetach();
            fragment_right = null;
        }
        if(fragment_middle != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.detach(fragment_middle).commit();
            fragment_middle.onDestroy();
            fragment_middle.onDetach();
            fragment_middle = null;
        }
    }

    public void roomWasDeleted() {
        if(fragment_right != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.detach(fragment_right).commit();
            fragment_right.onDestroy();
            fragment_right.onDetach();
            fragment_right = null;
        }
    }

    public void initDevicesFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("inTablet", true);

        if(fragment_right != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.detach(fragment_right).commit();
            fragment_right.onDestroy();
            fragment_right.onDetach();
            fragment_right = null;
        }

        FragmentTransaction transaction;
        fragment_right = new DevicesListFragment();
        fragment_right.setArguments(bundle);
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tablet_3, fragment_right).commit();
    }
}
