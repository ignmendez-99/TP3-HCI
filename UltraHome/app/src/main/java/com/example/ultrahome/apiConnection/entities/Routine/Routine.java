package com.example.ultrahome.apiConnection.entities.Routine;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Routine {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("actions")
    @Expose
    private List<ActionsItem> actions;
    @SerializedName("meta")
    @Expose
    private Object meta = new Object();



    public Routine(String name, List<ActionsItem> actions) {
        this.name = name;
        this.actions = actions;
    }

    // GETTERS
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<ActionsItem> getActions() {
        return actions;
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