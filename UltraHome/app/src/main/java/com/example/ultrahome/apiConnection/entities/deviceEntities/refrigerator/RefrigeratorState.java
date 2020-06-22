package com.example.ultrahome.apiConnection.entities.deviceEntities.refrigerator;

import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefrigeratorState extends DeviceState {

    @SerializedName("freezerTemperature")
    @Expose
    private Integer freezerTemperature;

    @SerializedName("temperature")
    @Expose
    private Integer temperature;

    @SerializedName("mode")
    @Expose
    private String mode;
}
