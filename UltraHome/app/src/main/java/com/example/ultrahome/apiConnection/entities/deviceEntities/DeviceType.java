package com.example.ultrahome.apiConnection.entities.deviceEntities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceType {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("powerUsage")
    @Expose
    private Integer powerUsage;

    public DeviceType(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public DeviceType(String id) {
        this.id = id;
    }
}
