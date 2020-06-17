package com.example.ultrahome.apiConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Error {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("description")
    @Expose
    private List<String> description = null;

    public Error() {
    }

    public Error(int code, List<String> description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
}
