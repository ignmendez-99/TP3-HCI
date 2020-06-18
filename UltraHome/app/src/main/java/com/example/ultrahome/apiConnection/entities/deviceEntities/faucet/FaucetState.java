package com.example.ultrahome.apiConnection.entities.deviceEntities.faucet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaucetState {

    @SerializedName("status")
    @Expose
    private String status;
}
