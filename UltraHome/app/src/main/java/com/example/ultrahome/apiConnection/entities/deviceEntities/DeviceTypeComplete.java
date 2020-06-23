package com.example.ultrahome.apiConnection.entities.deviceEntities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DeviceTypeComplete {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("powerUsage")
    @Expose
    private Integer powerUsage;

    @SerializedName("actions")
    @Expose
    private List<DeviceTypeAction> actions = new ArrayList<>();

    public DeviceTypeComplete(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public DeviceTypeComplete(String id) {
        this.id = id;
    }

    // GETTERS
    public String getId() {
        return id;
    }
    public List<DeviceTypeAction> getActions() { return actions; }
}