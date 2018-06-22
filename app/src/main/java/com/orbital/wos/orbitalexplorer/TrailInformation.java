package com.orbital.wos.orbitalexplorer;

public class TrailInformation {
    private String title;
    private String description;
    private String photouri;
    private int index;
    private int rating;

    public TrailInformation(String inputTitle, String inputDescription, String inputPhotouri, int inputIndex, int inputRating) {
        this.title = inputTitle;
        this.description = inputDescription;
        this.photouri = inputPhotouri;
        this.index = inputIndex;
        this.rating = inputRating;
    }

    public TrailInformation() {

    }

    public int getIndex() {
        return index;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getPhotouri() {
        return photouri;
    }

    public int getRating() {
        return rating;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhotouri(String photouri) {
        this.photouri = photouri;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
