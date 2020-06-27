package com.example.ultrahome.apiConnection.entities.deviceEntities.speaker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpeakerState {

    private class Song {
        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("artist")
        @Expose
        private String artist;

        @SerializedName("album")
        @Expose
        private String album;

        @SerializedName("duration")
        @Expose
        private String duration;

        @SerializedName("progress")
        @Expose
        private String progress;

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        public String getAlbum() {
            return album;
        }

        public String getDuration() {
            return duration;
        }

        public String getProgress() {
            return progress;
        }
    }


    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("volume")
    @Expose
    private Integer volume;

    @SerializedName("genre")
    @Expose
    private String genre;

    @SerializedName("song")
    @Expose
    private Song song;


    public String getStatus() {
        return status;
    }

    public Integer getVolume() {
        return volume;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return song.getTitle();
    }

    public String getArtist() {
        return song.getArtist();
    }

    public String getAlbum() {
        return song.getAlbum();
    }

    public String getDuration() {
        return song.getDuration();
    }

    public String getProgress() {
        return song.getProgress();
    }
}
