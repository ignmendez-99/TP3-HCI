package com.example.ultrahome.apiConnection.entities.deviceEntities.speaker;

import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpeakerState extends DeviceState {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("volume")
    @Expose
    private Integer volume;

    @SerializedName("genre")
    @Expose
    private Integer genre;
}
