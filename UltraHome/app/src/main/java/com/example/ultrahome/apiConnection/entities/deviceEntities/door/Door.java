package com.example.ultrahome.apiConnection.entities.deviceEntities.door;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Door extends Device {

    @SerializedName("state")
    @Expose
    private DoorState state;

    public Door(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }
}
