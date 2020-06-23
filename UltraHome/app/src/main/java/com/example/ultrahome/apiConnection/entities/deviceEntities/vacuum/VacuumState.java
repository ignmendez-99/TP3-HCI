package com.example.ultrahome.apiConnection.entities.deviceEntities.vacuum;

import com.example.ultrahome.apiConnection.entities.deviceEntities.DeviceState;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VacuumState extends DeviceState {

    private class Location {
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("mode")
    @Expose
    private String mode;

    @SerializedName("batteryLevel")
    @Expose
    private Integer batteryLevel;

    @SerializedName("location")
    @Expose
    private Location location;

    public String getStatus() {
        return status;
    }

    public String getMode() {
        return mode;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public Location getLocation() {
        return location;
    }
}
