package com.knowledgenest.Model;

public class CourseModel {
    private String title, duration, rating, description;
    private long price;
    private String thumbnail, introvideo, postId, postedBy, enable;

    public CourseModel() {

    }

    public CourseModel(String title) {
        this.title = title;
    }

    public CourseModel(String title, String duration, String rating, String description,
                       long price, String thumbnail, String introvideo,
                       String postedBy, String enable) {
        this.title = title;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
        this.price = price;
        this.thumbnail = thumbnail;
        this.introvideo = introvideo;
        this.postedBy = postedBy;
        this.enable = enable;
    }


    public void copyFrom(CourseModel other) {
        if (other == null) return;
        this.title = other.title;
        this.duration = other.duration;
        this.rating = other.rating;
        this.description = other.description;
        this.price = other.price;
        this.thumbnail = other.thumbnail;
        this.introvideo = other.introvideo;
        this.postId = other.postId;
        this.postedBy = other.postedBy;
        this.enable = other.enable;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getIntrovideo() {
        return introvideo;
    }

    public void setIntrovideo(String introvideo) {
        this.introvideo = introvideo;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
}
