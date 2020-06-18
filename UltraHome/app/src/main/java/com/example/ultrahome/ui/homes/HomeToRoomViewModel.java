package com.example.ultrahome.ui.homes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeToRoomViewModel extends ViewModel {

    private MutableLiveData<String> homeId = new MutableLiveData<>();

    public LiveData<String> getHomeId() {
        return homeId;
    }

    public void storeHomeId(String item) {
        homeId.setValue(item);
    }
}