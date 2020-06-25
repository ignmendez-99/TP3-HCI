package com.example.ultrahome.ui.homes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class HomesViewModel extends ViewModel {

    private MutableLiveData<Map<Integer, Integer>> roomsInEachHome = new MutableLiveData<>();

    public LiveData<Map<Integer, Integer>> getRoomsInEachHome() {
        return roomsInEachHome;
    }

    public void storeRoomsInEachHome(Map<Integer, Integer> item) {
        roomsInEachHome.setValue(item);
    }
}
