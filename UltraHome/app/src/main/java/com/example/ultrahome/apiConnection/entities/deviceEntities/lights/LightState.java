package com.example.ultrahome.apiConnection.entities.deviceEntities.lights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LightState {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("brightness")
    @Expose
    private Integer brightness;

    public String getStatus() {
        return status;
    }

    public String getColor() {
        return color;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public boolean isOn() {
        return status.equals("on");
    }

    public boolean isOff() {
        return status.equals("off");
    }
}
