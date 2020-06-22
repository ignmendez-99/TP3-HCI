package com.example.ultrahome.apiConnection.entities.deviceEntities.blinds;

import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlindsState extends DeviceState {

    // todo: no se si esto nos termine sirviendo al final...
    static final String[] possibleStatus = {"closing", "closed", "opened", "opening"};

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("level")
    @Expose
    private Integer level;

    @SerializedName("currentLevel")
    @Expose
    private Integer currentLevel;

    public String getStatus() {
        return status;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
