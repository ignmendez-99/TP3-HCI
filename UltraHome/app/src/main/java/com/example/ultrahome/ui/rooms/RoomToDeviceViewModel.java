package com.example.ultrahome.ui.rooms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class RoomToDeviceViewModel extends ViewModel {

    private MutableLiveData<String> roomId = new MutableLiveData<>();

    // These 2 variables are only used by the Vacuum
    private MutableLiveData<List<String>> roomIds = new MutableLiveData<>();
    private MutableLiveData<List<String>> roomNames = new MutableLiveData<>();

    // GETTERS
    public LiveData<String> getRoomId() {
        return roomId;
    }
    public LiveData<List<String>> getRoomIds() {
        return roomIds;
    }
    public LiveData<List<String>> getRoomNames() {
        return roomNames;
    }

    // SETTERS
    public void storeRoomId(String item) {
        roomId.setValue(item);
    }
    public void storeRoomIds(List<String> item) {
        roomIds.setValue(item);
    }
    public void storeRoomNames(List<String> item) {
        roomNames.setValue(item);
    }
}
