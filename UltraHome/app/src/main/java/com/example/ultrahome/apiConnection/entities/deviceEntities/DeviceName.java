package com.example.ultrahome.apiConnection.entities.deviceEntities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceName {
    @SerializedName("name")
    @Expose
    protected String name;

    @SerializedName("meta")
    @Expose
    protected Object meta = new Object();

    public DeviceName() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
