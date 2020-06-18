package com.example.ultrahome.apiConnection.entities.deviceEntities.vacuum;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vacuum extends Device {

    @SerializedName("state")
    @Expose
    private VacuumState state;

    public Vacuum(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }
}
