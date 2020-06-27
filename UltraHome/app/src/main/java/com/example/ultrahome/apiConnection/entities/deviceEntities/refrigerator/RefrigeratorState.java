package com.example.ultrahome.apiConnection.entities.deviceEntities.refrigerator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefrigeratorState {

    @SerializedName("freezerTemperature")
    @Expose
    private Integer freezerTemperature;

    @SerializedName("temperature")
    @Expose
    private Integer temperature;

    @SerializedName("mode")
    @Expose
    private String mode;

    public Integer getFreezerTemperature() {
        return freezerTemperature;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public String getMode() {
        return mode;
    }
}
