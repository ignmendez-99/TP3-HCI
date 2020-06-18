package com.example.ultrahome.apiConnection.entities.deviceEntities.speaker;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Speaker extends Device {

    @SerializedName("state")
    @Expose
    private SpeakerState state;

    public Speaker(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }
}
