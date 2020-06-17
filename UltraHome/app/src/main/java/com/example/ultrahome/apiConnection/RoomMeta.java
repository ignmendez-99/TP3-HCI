package com.example.ultrahome.apiConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomMeta {

    @SerializedName("size")
    @Expose
    private String size;

    public RoomMeta(String size) {
        this.size = size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return this.size;
    }

    @Override
    public String toString() {
        return this.getSize();
    }
}
