package com.example.ultrahome.apiConnection.entities.deviceEntities.blinds;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlindsState {

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
}
