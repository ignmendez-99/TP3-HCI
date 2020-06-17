package com.example.ultrahome.ui.devices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DevicesViewModel extends ViewModel {

    private MutableLiveData<String> mText = new MutableLiveData<>();

    // used to get the "parameter"
    public LiveData<String> getText() {
        return mText;
    }

    // used to "set" the "parameter"
    public void select(String item) {
        mText.setValue(item);
    }
}