package com.example.ultrahome.ui.rooms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoomToDeviceViewModel extends ViewModel {

    private MutableLiveData<String> roomId = new MutableLiveData<>();

    public LiveData<String> getRoomId() {
        return roomId;
    }

    public void storeRoomId(String item) {
        roomId.setValue(item);
    }
}
