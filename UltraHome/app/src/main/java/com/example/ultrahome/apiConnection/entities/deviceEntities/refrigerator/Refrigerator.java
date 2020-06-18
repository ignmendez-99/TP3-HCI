package com.example.ultrahome.apiConnection.entities.deviceEntities.refrigerator;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Refrigerator extends Device {

    @SerializedName("state")
    @Expose
    private RefrigeratorState state;

    public Refrigerator(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }
}
