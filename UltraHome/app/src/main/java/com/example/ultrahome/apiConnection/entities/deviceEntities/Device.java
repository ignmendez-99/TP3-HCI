package com.example.ultrahome.apiConnection.entities.deviceEntities;

import com.example.ultrahome.apiConnection.entities.Room;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class Device {

    @SerializedName("id")
    @Expose
    protected String id;

    @SerializedName("name")
    @Expose
    protected String name;

    @SerializedName("meta")
    @Expose
    protected Object meta = new Object();

    @SerializedName("room")
    @Expose
    protected Room room;

    @SerializedName("type")
    @Expose
    protected DeviceType type;

    // GETTERS
    public String getId() {
        return id;
    }
    public Room getRoom() {
        return room;
    }
    public String getName() {
        return name;
    }
    public DeviceType getType() {
        return type;
    }
}
