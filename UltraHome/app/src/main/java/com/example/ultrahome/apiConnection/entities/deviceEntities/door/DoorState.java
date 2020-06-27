package com.example.ultrahome.apiConnection.entities.deviceEntities.door;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DoorState {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("lock")
    @Expose
    private String lock;

    public boolean isLocked() {
        return lock.equals("locked");
    }

    public boolean isUnlocked() {
        return lock.equals("unlocked");
    }

    public boolean isOpen() {
        return status.equals("opened");
    }

    public boolean isClose() {
        return status.equals("closed");
    }
}
