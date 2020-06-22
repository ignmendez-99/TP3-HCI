package com.example.ultrahome.apiConnection.entities.deviceEntities.lights;

import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LightState extends DeviceState {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("brightness")
    @Expose
    private Integer brightness;
}
