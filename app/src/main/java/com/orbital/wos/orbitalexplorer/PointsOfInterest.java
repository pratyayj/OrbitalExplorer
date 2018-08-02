package com.orbital.wos.orbitalexplorer;

public class PointsOfInterest {
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private String placeID;
    private int index;

    public PointsOfInterest(String inputTitle, String inputDescription, double inputLatitude, double inputLongitude, String inputPlaceID, int inputIndex) {
        this.title = inputTitle;
        this.description = inputDescription;
        this.latitude = inputLatitude;
        this.longitude = inputLongitude;
        this.placeID = inputPlaceID;
        this.index = inputIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PointsOfInterest) {
            PointsOfInterest p = (PointsOfInterest) obj;
            return getLatitude() == p.getLatitude();
        }
        return false;
    }

    public PointsOfInterest() {}

    public String getPlaceID() {
        return placeID;
    }

    public int getIndex() {
        return index;
    }

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

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
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

    public void setIndex(int index) {
        this.index = index;
    }
}
