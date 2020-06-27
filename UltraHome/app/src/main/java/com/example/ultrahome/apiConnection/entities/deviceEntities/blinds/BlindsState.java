package com.example.ultrahome.apiConnection.entities.deviceEntities.blinds;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlindsState {
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
