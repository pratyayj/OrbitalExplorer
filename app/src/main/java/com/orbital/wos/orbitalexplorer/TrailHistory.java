package com.orbital.wos.orbitalexplorer;

/**
 * This class holds the information for a TrailHistory object (that forms a part of the User History
 * data).
 */
public class TrailHistory {
    private String title;
    private String photouri;
    private double latitude;
    private double longitude;

    public TrailHistory(String inputTitle, String inputPhotouri, double inputLatitude, double inputLongitude) {
        this.title = inputTitle;
        this.photouri = inputPhotouri;
        this.latitude = inputLatitude;
        this.longitude = inputLongitude;
    }

    public TrailHistory() {}

    public String getPhotouri() {
        return photouri;
    }

    public String getTitle() {
        return title;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPhotouri(String inputPhotouri) {
        this.photouri = inputPhotouri;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
