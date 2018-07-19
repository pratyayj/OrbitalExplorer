package com.orbital.wos.orbitalexplorer;

import android.graphics.Point;

public class PointOfInterest {
    String title;
    String description;
    double latitude;
    double longitude;

    public PointOfInterest(String inputTitle, String inputDescription, double inputLatitude, double inputLongitude) {
        this.title = inputTitle;
        this.description = inputDescription;
        this.latitude = inputLatitude;
        this.longitude = inputLongitude;
    }

    public PointOfInterest() {}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
