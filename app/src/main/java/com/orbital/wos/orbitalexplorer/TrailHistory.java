package com.orbital.wos.orbitalexplorer;

/**
 * This class holds the information for a TrailHistory object (that forms a part of the User History
 * data).
 */
public class TrailHistory {
    private String title;
    private String photouri;

    public TrailHistory(String inputTitle, String inputPhotouri) {
        this.title = inputTitle;
        this.photouri = inputPhotouri;
    }

    public TrailHistory() {}

    public String getPhotouri() {
        return photouri;
    }

    public String getTitle() {
        return title;
    }

    public void setPhotouri(String inputPhotouri) {
        this.photouri = inputPhotouri;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
