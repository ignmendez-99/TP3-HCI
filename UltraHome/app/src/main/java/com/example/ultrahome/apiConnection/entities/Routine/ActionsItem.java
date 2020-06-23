package com.example.ultrahome.apiConnection.entities.Routine;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ActionsItem {

    @SerializedName("device")
    @Expose
    private Device device;

    @SerializedName("actionName")
    @Expose
    private String actionName;

    @SerializedName("params")
    @Expose
    private List<String> params = new ArrayList<>();

    @SerializedName("meta")
    @Expose
    private Object meta = new Object();
}
