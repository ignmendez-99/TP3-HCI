package com.example.ultrahome.apiConnection.entities.deviceEntities.vacuum;

import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VacuumState extends DeviceState {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("mode")
    @Expose
    private String mode;

    @SerializedName("batteryLevel")
    @Expose
    private Integer batteryLevel;

    @SerializedName("location")
    @Expose
    private Integer location;
}
