package com.example.ultrahome.apiConnection.entities.deviceEntities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Lights extends Device {

    @SerializedName("state")
    @Expose
    private LightState state;

    public Lights(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }
}
