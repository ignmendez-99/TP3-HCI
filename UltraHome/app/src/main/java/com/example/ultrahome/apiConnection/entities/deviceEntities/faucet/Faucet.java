package com.example.ultrahome.apiConnection.entities.deviceEntities.faucet;

import com.example.ultrahome.apiConnection.entities.deviceEntities.Device;
import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Faucet extends Device {

    @SerializedName("state")
    @Expose
    private FaucetState state;

    public Faucet(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }
}
