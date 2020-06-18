package com.example.ultrahome.apiConnection.entities.deviceEntities.blinds;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Blinds extends Device {

    @SerializedName("state")
    @Expose
    private BlindsState state;

    public Blinds(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }
}
