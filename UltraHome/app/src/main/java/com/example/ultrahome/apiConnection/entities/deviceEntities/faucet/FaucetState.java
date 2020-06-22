package com.example.ultrahome.apiConnection.entities.deviceEntities.faucet;

import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaucetState extends DeviceState {

    @SerializedName("status")
    @Expose
    private String status;
}
