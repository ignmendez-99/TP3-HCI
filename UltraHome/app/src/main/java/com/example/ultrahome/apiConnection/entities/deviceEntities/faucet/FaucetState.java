package com.example.ultrahome.apiConnection.entities.deviceEntities.faucet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaucetState {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("quantity")
    @Expose
    private Integer quantity;

    @SerializedName("unit")
    @Expose
    private String unit;

    @SerializedName("dispensedQuantity")
    @Expose
    private Double dispensedQuantity;

    public boolean isOpen() {
        return status.equals("opened");
    }

    public boolean isClosed() {
        return status.equals("closed");
    }

    public String getStatus() {
        return status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public Double getDispensedQuantity() {
        return dispensedQuantity;
    }
}
