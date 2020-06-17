package com.example.ultrahome.apiConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResult {

    @SerializedName("error")
    @Expose
    private Error error;

    public ErrorResult() {
    }

    public ErrorResult(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
