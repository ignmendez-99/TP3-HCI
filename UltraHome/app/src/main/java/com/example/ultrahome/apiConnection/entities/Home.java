package com.example.ultrahome.apiConnection.entities;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Home {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("meta")
    @Expose
    private Object meta = new Object();

    public Home(String name) {
        this.name = name;
    }

    // GETTERS
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    // SETTERS
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        if (this.getId() != null)
            return String.format("%s - %s", this.getId(), this.getName());
        else
            return this.getName();
    }
}
