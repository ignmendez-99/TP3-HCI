package com.example.ultrahome.apiConnection;

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
    private HomeMeta meta;

    public Home() {
    }

    public Home(String name, HomeMeta meta) {
        this.name = name;
        this.meta = meta;
    }

    public Home(String id, String name, HomeMeta meta) {
        this.id = id;
        this.name = name;
        this.meta = meta;
    }

    // GETTERS
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public HomeMeta getMeta() {
        return meta;
    }

    // SETTERS
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setMeta(HomeMeta meta) {
        this.meta = meta;
    }

    @NonNull
    @Override
    public String toString() {
        if (this.getId() != null)
        {
            if (this.getMeta() != null)
                return String.format("%s - %s - %s", this.getId(), this.getName(), this.getMeta());
            else
                return String.format("%s - %s", this.getId(), this.getName());
        }
        else
        {
            if (this.getMeta() != null)
                return String.format("%s - %s", this.getName(), this.getMeta());
            else
                return this.getName();
        }
    }

    public static class HomeMeta {

        @SerializedName("size")
        @Expose
        private String size;

        public HomeMeta(String size) {
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
}
