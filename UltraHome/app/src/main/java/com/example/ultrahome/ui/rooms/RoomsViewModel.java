package com.example.ultrahome.ui.rooms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class RoomsViewModel extends ViewModel {

    private MutableLiveData<Map<Integer, Integer>> devicesInEachRoom = new MutableLiveData<>();

    public LiveData<Map<Integer, Integer>> getDevicesInEachRoom() {
        return devicesInEachRoom;
    }

    public void storeDevicesInEachRoom(Map<Integer, Integer> item) {
        devicesInEachRoom.setValue(item);
    }
}
