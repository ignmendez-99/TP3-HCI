package com.example.ultrahome.ui.devices;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DevicesListViewModel extends ViewModel {

    private MutableLiveData<Fragment> childFragment = new MutableLiveData<>();

    public LiveData<Fragment> getChildFragment() {
        return childFragment;
    }

    public void storeChildFragment(Fragment item) {
        childFragment.setValue(item);
    }
}
