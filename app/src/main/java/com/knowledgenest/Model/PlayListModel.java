package com.knowledgenest.Model;

public class PlayListModel {
    private String key;
    private String title;
    private String videoUrl;

    public PlayListModel() {
        // Empty constructor required for Firebase
    }

    public PlayListModel(String key, String title, String videoUrl) {
        this.key = key;
        this.title = title;
        this.videoUrl = videoUrl;
    }

    // Added safely for Composite Pattern (used for duplication)
    public void copyFrom(PlayListModel other) {
        if (other == null) return;
        this.key = other.key;
        this.title = other.title;
        this.videoUrl = other.videoUrl;
    }

    // ---------------- Getters and Setters ---------------- //
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
