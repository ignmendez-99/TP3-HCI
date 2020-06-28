package com.example.ultrahome.apiConnection.entities.deviceEntities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceTypeAction {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("params")
    @Expose
    private List<String> params;


    public DeviceTypeAction(String name) {
        this.name = name;
    }


    // GETTERS
    public String getName() { return name; }

}
