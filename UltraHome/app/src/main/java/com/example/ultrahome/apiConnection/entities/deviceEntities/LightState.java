package com.example.ultrahome.apiConnection.entities.deviceEntities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LightState{

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
